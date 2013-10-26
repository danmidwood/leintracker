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

(def ^:private analytics ((html/snippet "leintracker/web/html/ga.html" [:script] [_]) {}) )

(def ^:private home-button
  {:title "Home page"
   :href "#home"
   :text "Home"})

(def ^:private features-button
  {:title "See what we can do"
   :href "#features"
   :text "Features"
   :brand "brand-4"})

(def ^:private about-button
  {:title "Who we are"
   :href "#about"
   :text "About us"
   :brand "brand-4"})

(defn ^:private login-button [user]
  (if user
    {:id "username"
     :title "Sign out"
     :href "/logout"
     :icon "icon-github"
     :brand "brand-4"
     :text user}
    {:id "username"
     :title "Sign in"
     :href "/login"
     :icon "icon-github"
     :brand "brand-4"
     :text "Sign in"}))

(defn ^:private internal? [href]
  (= \# (first href)) "scroll")

(html/defsnippet nav-entry "leintracker/web/html/nav-entry.html" [:li]
  [{:keys [id title href brand icon text selected?]}]
  [:li] (html/add-class (when selected? "active"))
  [:li :a] (html/do->
            (html/content (when icon {:tag :i :attrs {:class icon} :content " "}))
            (html/add-class brand
                            (when (internal? href) "scroll"))
            (html/set-attr :id id)
            (html/set-attr :title title)
            (html/set-attr :href href)
            (html/append text)))

(html/defsnippet nav-bar "leintracker/web/html/navbar.html" {[:a] [:header]} [user]
  [:ul.nav] (html/do->
              (html/append (nav-entry (merge home-button {:selected true})))
              (html/append (nav-entry features-button))
              (html/append (nav-entry about-button))
              (html/append (nav-entry (login-button user)))))



(html/defsnippet home-page "leintracker/web/html/home.html" [:#home] [])
(html/defsnippet about-page "leintracker/web/html/about.html" [:#about] [])
(html/defsnippet features-page "leintracker/web/html/features.html" [:#features] [])
(html/defsnippet page-split "leintracker/web/html/page-splitter.html" [:.split] [])

(html/deftemplate index "leintracker/web/html/template.html"
  [{:keys [title logo user]}]
  [:head] (html/append analytics)
  [:title] (maybe-content title)
  [:h1.logo] (maybe-content logo)
  [:body] (html/do->
           (html/append (nav-bar user))
           (html/append (home-page))
           (html/append (page-split))
           (html/append (features-page))
           (html/append (about-page))))
