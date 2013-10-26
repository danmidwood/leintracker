(ns leintracker.web.views.common
  (:require [net.cgrand.enlive-html :as html]))


(defmacro maybe-content
  ([expr] `(if-let [x# ~expr] (html/content x#) identity))
  ([expr & exprs] `(maybe-content (or ~expr ~@exprs))))

(defn login-button [user]
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


(def analytics ((html/snippet "leintracker/web/html/ga.html" [:script] [_]) {}) )

(defn select [button]
  (merge button {:selected true}))

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

(html/defsnippet nav-bar "leintracker/web/html/navbar.html" {[:a] [:header]} [user entries]
  [:ul.nav] entries)
