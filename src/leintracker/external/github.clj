(ns leintracker.external.github
  (:use compojure.core)
  (:require
   [clj-http.client :as http]
   [cheshire.core :as json]
   [ring.util.response :as resp]
   [ring.util.codec :as codec]
   [environ.core :refer [env]]
   [clojure.java.io :as io]
   [tentacles.core :as gh.core]
   [tentacles.users :as gh.users]
   [tentacles.repos :as gh.repos]))

(defn- head-github
  [endpoint access-token]
  (-> (format "https://api.github.com%s%s&access_token=%s"
              endpoint
              (when-not (.contains endpoint "?") "?")
              access-token)
      (http/head {:throw-exceptions false})))

(defn- make-github-url [user project]
  (str "https://raw.github.com/" user "/" project "/master/project.clj"))

(defn get-lein-file-reader [user project]
  (let [location (make-github-url user project)]
    (io/reader location)))

(defn file-exists? [identity file {:keys [full-name]}]
  (->> (:current identity)
       (head-github (format "/repos/%s/contents%s" full-name file))
       :status
       (= 200)))

(defn ^:private as-oauth [identity]
  (clojure.set/rename-keys identity {:current :oauth-token}))


(defn user-name [identity]
  (-> (as-oauth identity)
      gh.users/me
      :name))

(defn ^:private underscores-to-hyphens
  "Change all underscore_keywords to hyphen_keywords"
  {:added "1.1"}
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(keyword (.replace (name k) "_" "-")) v] [k v]))]
    ;; only apply to maps
    (clojure.walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn ^:private get-all-pages [request]
  (assoc request :all-pages true))

(defn repos [identity]
  (->> (as-oauth identity)
      get-all-pages
      gh.repos/repos
      (remove empty?) ; for some reason we have empty records come through
      underscores-to-hyphens))
