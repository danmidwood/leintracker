(ns leintracker.web.web
  (:require [compojure.core :refer [defroutes routes GET POST PUT]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as rparams]
            [ring.middleware.reload :as rreload]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [leintracker.core :as core]
            [leintracker.web.views :as views]
            [leintracker.web.auth :as auth]
            [net.cgrand.enlive-html :as html]))

(defn render-home
  ([identity]
     (let [data (core/home-page identity)
           body (views/index data)]
       {:status 200
        :body (apply str body)})))

(defroutes base
  (GET "/" req
       (render-home (auth/read-identity req)))
  (route/resources "/" {:root "the-story"})
  (route/not-found "Not Found"))

(defroutes signed-in
  (GET "/user/:user" [user & req]
       {:status 200
        :body (apply str (views/index {:user user}))})
  (GET "/user/:user/project/:project/dependencies" [user project]
       {:status 200
        :body (json/generate-string (core/find-dependencies user project))})
  )

(def app
  (-> (routes signed-in auth/auth-routes base)
                                        ;(asset-pipeline config-options)
      (handler/site)
      (rparams/wrap-params)
      (rreload/wrap-reload '(leintracker.web.web
                             leintracker.core
                             leintracker.web.auth
                             leintracker.external.github
                             leintracker.external.lein))))
