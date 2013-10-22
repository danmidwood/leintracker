(ns dependencything.web
  (:require [compojure.core :refer [defroutes GET POST PUT]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as rparams]
            [ring.middleware.reload :as rreload]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [dependencything.lein :as dtlein]
            [net.cgrand.enlive-html :as html]))

(defmacro defjson [name args & body]
  `(defn ~name ~args (json/generate-string ~@body)))

(defn make-github-url [user project]
  (str "https://raw.github.com/" user "/" project "/master/project.clj"))

(def default-home {:title "LeinTracker"
                   :logo "LeinTracker Beta"
                   :page-headline "Keep your Clojure dependencies in line."
                   :features-headline "Why should you use LeinTracker"})

(html/deftemplate notmerged-index "dependencything/home.html"
  [ctxt]
  [:title] (html/content (:title ctxt))
  [:h1.logo] (html/content (:logo ctxt))
  [:h2#home-headline] (html/content (:page-headline ctxt))
  [:h2#features-headline] (html/content (:features-headline ctxt))
;  [:input#username] (html/set-attr :value (:user ctxt ""))
  )

(defn index [ctxt]
  (notmerged-index (merge default-home ctxt)))

;; (html/deftemplate index "dependencything/report.html"
;;   [ctxt]
;;   [:input#username] (html/set-attr :value (:user ctxt "")))



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
       (let [body (index {:user ""})]
             {:status 200
              :body (apply str body)}))
  (GET "/user/:user" [user]
       {:status 200
        :body (apply str (index {:user user}))})

  (GET "/user/:user/project/:project/dependencies" [user project]
          {:status 200
           :body (json/generate-string (find-dependencies user project))})
  (route/resources "/" {:root "the-story"})
  (route/not-found "Not Found"))



(def app
  (-> app-routes
      ;(asset-pipeline config-options)
      (handler/site)
      (rparams/wrap-params)
      (rreload/wrap-reload '(dependencything.web))))
