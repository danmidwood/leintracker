(ns leintracker.web.views.home
  (:require [net.cgrand.enlive-html :as html]
            [leintracker.web.views.common :as common]))

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

(html/defsnippet home-page "leintracker/web/html/home.html" [:#home] [])

(html/defsnippet about-page "leintracker/web/html/about.html" [:#about] [])

(html/defsnippet features-page "leintracker/web/html/features.html" [:#features] [])

(html/defsnippet page-split "leintracker/web/html/page-splitter.html" [:.split] [])

(defn ^:private build-nav-bar [user]
  (html/do->
   (html/append (common/nav-entry (merge home-button {:selected true})))
   (html/append (common/nav-entry features-button))
   (html/append (common/nav-entry (common/login-button user)))))

(defn ^:private build-home-body [user]
  (html/do->
   (html/append (common/nav-bar user (build-nav-bar user)))
   (html/append (home-page))
   (html/append (page-split))
   (html/append (features-page))))

(html/deftemplate index "leintracker/web/html/template.html"
  [{:keys [title logo user]}]
  [:head] (html/append common/analytics)
  [:title] (common/maybe-content title)
  [:h1.logo] (common/maybe-content logo)
  [:body] (build-home-body user))
