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

(html/defsnippet dependency-row "leintracker/web/html/dependency-row.html" [:tr]
  [{:keys [name current-version latest-version]}]
  [:tr] (html/do->
         (html/append {:tag :td :content [name]})
         (html/append {:tag :td :content [current-version]})
         (html/append {:tag :td :content [latest-version]})
         (html/append {:tag :td :content [(= current-version latest-version)]})))

(defn ^:private dependency-rows [dependencies]
  (map dependency-row dependencies))

(html/defsnippet repository "leintracker/web/html/repository.html" [:.accordion-group]
  [{:keys [name description dependencies]}]
  [:.accordion-toggle] (html/do->
                        (html/content name)
                        (html/set-attr :href (str "#" name)))
  [:.accordion-body] (html/set-attr :id name)
  [:.accordion-body :.accordion-inner :h4] (html/content description)
  [:.accordion-body :.accordion-inner :table] (html/add-class "color-4")
  [:.accordion-body :.accordion-inner :table :tbody] (html/append (dependency-rows dependencies)))


(defn ^:private span [size content]
  {:tag :div
   :attrs {:class (str "span" size)}
   :content content})


(html/defsnippet repositories-page "leintracker/web/html/repositories.html" [:#repositories]
  [repos]
  [:div.inner-page] (html/prepend (span 3 (str "You have " (count repos) " repositories")))
  [:.accordion] (apply html/do-> (map (comp html/append repository) repos)))

(defn ^:private build-nav-bar [user]
  (html/do->
   (html/append (common/nav-entry home-button))
   (html/append (common/nav-entry (common/select repositories-button)))
   (html/append (common/nav-entry (common/login-button user)))))

(defn ^:private build-repos-body [user repos]
  (html/do->
   (html/append (common/nav-bar user (build-nav-bar user)))
   (html/append (repositories-page repos))
   (html/append (common/footer))))

(html/deftemplate index "leintracker/web/html/template.html"
  [{:keys [title logo user repos]}]
  [:head] (html/do->
           (html/append (common/styles))
           (html/append common/analytics))
  [:title] (common/maybe-content title)
  [:h1.logo] (common/maybe-content logo)
  [:body] (build-repos-body user repos))
