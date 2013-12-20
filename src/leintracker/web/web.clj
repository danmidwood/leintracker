(ns leintracker.web.web
  (:require [compojure.core :refer [defroutes routes GET POST PUT]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as rresponse]
            [ring.middleware.reload :as rreload]
            [clojure.java.io :as io]
            [leintracker.core :as core]
            [leintracker.web.views.home :as views.home]
            [leintracker.web.views.repos :as views.repos]
            [leintracker.web.auth :as auth]
            [net.cgrand.enlive-html :as html]
            [cornet.core :as cornet]
            [cornet.route :as croute]
            [taoensso.timbre :as log]
            [cheshire.core :as json]))

(def ^:private underscore-opts
  {:key-fn (fn [k] (-> (name k)
                       (clojure.string/replace "-" "_")))})

(defn render-home
  ([]
     (let [body (views.home/index nil)]
       {:status 200
        :body (apply str body)})))

(defn single-page
  ([id]
     (let [data (core/repos-page id)
           body (views.repos/single-page data)]
       {:status 200
        :body (apply str body)})))


(defroutes signed-in
  (GET "/" req
       (if-let [id (auth/read-identity req)]
         (single-page id)
         (render-home)))
  (GET "/repos" [:as req]
       (-> {:status 200
            :body (-> (core/get-repos (auth/read-identity req))
                      (json/generate-string underscore-opts
                                            ))}
           (rresponse/content-type "application/json")))
  (GET "/repos/:user/:repo/project" [user repo :as req]
       (let [file (core/project-file (auth/read-identity req)
                                 user
                                 repo)]
         (rresponse/content-type
          {:status 200
           :body (json/generate-string {:project_file file})}
          "application/json"))
)
  (GET "/repos/:user/project/:project/dependencies" [user project]
       (do  (log/info "Getting repos")
            {:status 200
             :body (->> (core/find-dependencies {:full-name (str user
                                                                 "/"
                                                                 project)})
                        views.repos/dependency-rows
                        (map html/emit*)
                        flatten
                        clojure.string/join)})))

(defroutes cornet
  (croute/wrap-url-response
   (some-fn
    (cornet.processors.lesscss/wrap-lesscss-processor (cornet.loader/resource-loader "the-story/assets/less")
                                                      :mode :dev)
    (cornet/static-assets-loader "the-story"
                                 :from-filesystem false
                                 :mode :dev)))

  (route/not-found "Not Found"))

(defn log-exceptions [f]
  (fn [request]
    (try (f request)
      (catch Exception e
        (log/error e)
        (throw e)))))

(def app
  (-> (routes signed-in auth/auth-routes cornet)
      (handler/site)
      (log-exceptions)
      (rreload/wrap-reload '(leintracker.web.web
                             leintracker.core
                             leintracker.web.auth
                             leintracker.external.github
                             leintracker.external.lein
                             leintracker.web.views.home
                             leintracker.web.views.repos))))
