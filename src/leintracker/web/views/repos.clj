(ns leintracker.web.views.repos
  (:require [net.cgrand.enlive-html :as html]
            [leintracker.web.views.common :as common]))

(def ^:private home-button
  {:title "Home page"
   :href "/"
   :text "Home"})

(def ^:private repositories-button
  {:title "User repositories"
   :href "#repositories"
   :text "Repositories"
   :brand "brand-4"})

(html/defsnippet repositories-page "leintracker/web/html/repositories.html" [:#repositories] [])

(defn ^:private build-nav-bar [user]
  (html/do->
   (html/append (common/nav-entry home-button))
   (html/append (common/nav-entry (common/select repositories-button)))
   (html/append (common/nav-entry (common/login-button user)))))

(defn ^:private build-repos-body [user]
  (html/do->
   (html/append (common/nav-bar user (build-nav-bar user)))
   (html/append (repositories-page))))

(html/deftemplate index "leintracker/web/html/template.html"
  [{:keys [title logo user]}]
  [:head] (html/append common/analytics)
  [:title] (common/maybe-content title)
  [:h1.logo] (common/maybe-content logo)
  [:body] (build-repos-body user))
