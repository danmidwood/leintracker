(ns leintracker.web
  (:require [compojure.core :refer [defroutes routes GET POST PUT]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as rparams]
            [ring.middleware.reload :as rreload]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [leintracker.core :as core]
            [leintracker.auth :as dtauth]
            [net.cgrand.enlive-html :as html]))

(defmacro defjson [name args & body]
  `(defn ~name ~args (json/generate-string ~@body)))

(defmacro maybe-content
  ([expr] `(if-let [x# ~expr] (html/content x#) identity))
  ([expr & exprs] `(maybe-content (or ~expr ~@exprs))))

(html/deftemplate index "leintracker/home.html"
  [{:keys [title logo page-headline features-headline user]}]
  [:title] (maybe-content title)
  [:h1.logo] (maybe-content logo)
  [:h2#home-headline] (maybe-content page-headline)
  [:h2#features-headline] (maybe-content features-headline)
  [:a#username] (maybe-content user))

(defn render-home
  ([identity]
     (let [data (core/home-page identity)
           body (index data)]
       {:status 200
        :body (apply str body)})))

(defroutes base
  (GET "/" req
       (render-home (dtauth/read-identity req)))
  (route/resources "/" {:root "the-story"})
  (route/not-found "Not Found"))

(defroutes signed-in
  (GET "/user/:user" [user & req]
       {:status 200
        :body (apply str (index {:user user}))})
  (GET "/user/:user/project/:project/dependencies" [user project]
       {:status 200
        :body (json/generate-string (core/find-dependencies user project))})
  )

(def app
  (-> (routes signed-in dtauth/auth-routes base)
                                        ;(asset-pipeline config-options)
      (handler/site)
      (rparams/wrap-params)
      (rreload/wrap-reload '(leintracker.web
                             leintracker.core
                             leintracker.auth
                             leintracker.github
                             leintracker.lein))))
