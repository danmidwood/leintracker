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


(defn dependency-rows [dependencies]
  (map dependency-row dependencies))

(defn ^:private lazy-dependency-rows [id owner repo]
  {:tag :script :content (format "$( \"#%s\" ).load( \"repos/%s/project/%s/dependencies\" );" id owner repo)})

(defn repository-dispatch-fn [{:keys [dependencies is-lein?]}]
  (if is-lein?
    (if (nil? dependencies)
      :lein-no-deps
      :lein-with-deps)
    :not-lein))

(defmulti repository-data repository-dispatch-fn)

(defmacro defmethod-snippet
  "Creates and installs a new method of multimethod associated with the snippet."
  [multifn dispatch-val snippet]
  `(. ~(with-meta multifn {:tag 'clojure.lang.MultiFn}) addMethod ~dispatch-val ~snippet))




(defmethod-snippet repository-data :lein-no-deps
  (html/snippet "leintracker/web/html/repository.html" [:.accordion-group]
                [{:keys [name description owner]}]
                [:.accordion-group] (html/set-attr :id name)
                [:.accordion-toggle] (html/do->
                                      (html/content name)
                                      (html/set-attr :href (str "#" name "-description")))

                [:.accordion-body] (html/set-attr :id (str name "-description"))
                [:.accordion-body :.accordion-inner :h4] (html/content description)
                [:.accordion-body :.accordion-inner :table] (html/add-class "color-4")
                [:.accordion-body :.accordion-inner :table :tbody] (let [id (java.util.UUID/randomUUID)]
                                                                     (html/do->
                                                                      (html/set-attr :id (str id))
                                                                      (html/append (lazy-dependency-rows id (:login owner) name))))))

(defmethod-snippet repository-data :not-lein {})

(defmethod-snippet repository-data :lein-with-deps
  (html/snippet "leintracker/web/html/repository.html" [:.accordion-group]
                [{:keys [name description dependencies]}]
                [:.accordion-toggle] (html/content name)
                [:.accordion-group] (html/set-attr :id name)
                [:.accordion-body :.accordion-inner :h4] (html/content description)
                [:.accordion-body :.accordion-inner :table] (html/add-class "color-4")
                [:.accordion-body :.accordion-inner :table :tbody] (html/append (dependency-rows dependencies))))




(defmulti repository-name :is-lein?)

(defmethod-snippet repository-name :default
  (html/snippet "leintracker/web/html/repository-button.html" [:.accordion-group]
                [{:keys [name description owner]}]
                [:.accordion-toggle] (html/do->
                                      (html/content name)
                                      (html/set-attr :href (str "#" name)))))

(defn ^:private span [size content]
  {:tag :div
   :attrs {:class (str "span" size)}
   :content content})


(html/defsnippet repositories-page "leintracker/web/html/repositories.html" [:#repositories]
  [repos]
  [:.sidebar] (apply html/do->
                     (concat (map (comp html/append repository-name) (filter :is-lein? repos))
                             (map (comp html/append repository-name) (filter (comp not :is-lein?) repos))))
  [:.accordion] (apply html/do-> (map (comp html/append repository-data) repos)))

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
