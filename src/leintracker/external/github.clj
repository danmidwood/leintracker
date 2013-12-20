(ns leintracker.external.github
  (:use compojure.core)
  (:require
   [clj-http.client :as http]
   [cheshire.core :as json]
   [ring.util.response :as resp]
   [ring.util.codec :as codec]
   [clojure.java.io :as io]
   [taoensso.timbre :as log]
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

(defn- make-github-url [full-name]
  (str "https://raw.github.com/" full-name "/master/project.clj"))

(defn get-lein-file-reader [{:keys [full-name]}]
  (let [location (make-github-url full-name)]
    (io/reader location)))

(defn file-exists? [identity user repo file]
  (->> (:current identity)
       (head-github (log/spy :debug
                             "Checking for file:"
                             (format "/repos/%s/%s/contents%s" user repo file)))
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

(defn ^:private all-pages [request]
  (assoc request :all-pages true))

(defn ^:private public [request]
  (assoc request :type "public"))

(defn repos [identity]
  (->> (as-oauth identity)
       all-pages
       public
       gh.repos/repos
       (remove empty?) ; for some reason we have empty records come through
       underscores-to-hyphens))
