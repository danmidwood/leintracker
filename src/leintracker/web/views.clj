(ns leintracker.web.views
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as rparams]
            [ring.middleware.reload :as rreload]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [leintracker.web.auth :as dtauth]
            [net.cgrand.enlive-html :as html]))

(defmacro maybe-content
  ([expr] `(if-let [x# ~expr] (html/content x#) identity))
  ([expr & exprs] `(maybe-content (or ~expr ~@exprs))))

(def analytics ((html/snippet "leintracker/web/html/ga.html" [:script] [_]) {}) )

(html/defsnippet nav-entry "leintracker/web/html/nav-entry.html" [:li]
  [{:keys [id title href icon text]}]
  [:li :a] (html/set-attr :id id)
  [:li :a] (html/set-attr :title title)
  [:li :a] (html/set-attr :href href)
  [:li :a] (html/content {:tag :i :attrs {:class icon} :content " "})
  [:li :a] (html/append text))

(defn ^:private user-representation [user]
  (if user
    {:id "username"
     :title "Sign in"
     :href "/logout"
     :icon "icon-github"
     :text user}
    {:id "username"
     :title "Sign in"
     :href "/login"
     :icon "icon-github"
     :text "Sign in"}))

(html/deftemplate index "leintracker/web/html/home.html"
  [{:keys [title logo page-headline features-headline user]}]
  [:head] (html/append analytics)
  [:title] (maybe-content title)
  [:h1.logo] (maybe-content logo)
  [:h2#home-headline] (maybe-content page-headline)
  [:h2#features-headline] (maybe-content features-headline)
  [:ul.nav] (html/append (nav-entry (user-representation user))))
