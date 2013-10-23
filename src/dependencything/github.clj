(ns dependencything.github
  (:use compojure.core)
  (:require
   [clj-http.client :as http]
   [cheshire.core :as json]
   [ring.util.response :as resp]
   [ring.util.codec :as codec]
   [environ.core :refer [env]]
   [clojure.java.io :as io]))

(defn- call-github
  [endpoint access-token]
  (-> (format "https://api.github.com%s%s&access_token=%s"
              endpoint
              (when-not (.contains endpoint "?") "?")
              access-token)
      http/get
      :body
      (json/parse-string (fn [^String s] (keyword (.replace s \_ \-))))))

;; Go use an appropriate cache from https://github.com/clojure/core.cache
(def get-public-repos (partial call-github "/user/repos?type=private"))

(def get-user-name (comp :name (partial call-github "/user")))

(defn- make-github-url [user project]
  (str "https://raw.github.com/" user "/" project "/master/project.clj"))

(defn get-lein-file-reader [user project]
  (let [location (make-github-url user project)]
    (io/reader location)))

(defn get-github-name
  [identity]
  (get-user-name (:current identity)))
