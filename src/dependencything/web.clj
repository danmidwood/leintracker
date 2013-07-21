(ns dependencything.web
  (:require [compojure.core :refer [defroutes GET POST PUT]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as rparams]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [dependencything.lein :as dtlein]
            [net.cgrand.enlive-html :as html]))

(defmacro defjson [name args & body]
  `(defn ~name ~args (json/generate-string ~@body)))

(defn make-github-url [user project]
  (str "https://raw.github.com/" user "/" project "/master/project.clj"))

(html/deftemplate index "dependencything/home.html"
  [ctxt]
  [:input#username] (html/set-attr :value (:user ctxt)))


(defn find-dependencies [user project]
  (let [location (make-github-url user project)
        url (new java.net.URL location)
        url-stream (.openStream url)
        file (java.io.File/createTempFile project ".github")]
    (do
      (io/copy url-stream file)
      (.close url-stream)
      (dtlein/run (.getAbsolutePath file)))))

(defroutes app-routes
  (GET "/" [] 
       {:status 200
        :body (apply str (index {:user ""}))})
  (GET "/user/:user" [user] 
       {:status 200
        :body (apply str (index {:user user}))})

  (GET "/user/:user/project/:project/dependencies" [user project]
          {:status 200
           :body (json/generate-string (find-dependencies user project))})
  (route/resources "/")
  (route/not-found "Not Found"))



(def app
  (do (handler/site
       (rparams/wrap-params app-routes))))


