(ns leintracker.external.lein
  (:require [leiningen.core.project :as lein.project]
            [cemerick.pomegranate.aether :as aether]))

(def ^:dynamic *project* (lein.project/read))
(def ^:dynamic *aether-settings* {:repositories (*project* :repositories) :retrieve true})

(defn read-dependencies
  "Read dependencies from a project file as a list of maps containing :name and :version"
  []
  (let [dependencies (*project* :dependencies)
        to-map (fn [[name version]] {:name name :version version})]
    (map to-map dependencies)))

(defn-  aether-resolve
  [settings]
  (apply aether/resolve-dependencies
         (apply concat (merge *aether-settings* settings))))


(defn find-dependencies
  [{name :name, version :version}]
  (println "Finding for " name version)
  (aether-resolve {:coordinates [[name version]]}))

(defn find-latest-deps
  [{dep-group-and-name :name}]
  (let [deps (aether-resolve {:coordinates [[dep-group-and-name "LATEST"]]})]
    deps))

(defn find-latest-version [{dep-group-and-name :name}]
  (let [deps (find-latest-deps {:name dep-group-and-name})
        keys (keys deps)
        dep-name-symbol (symbol (name dep-group-and-name))
        reducer-fn (fn [a] (or (= (first a) dep-group-and-name) (= (first a) dep-name-symbol)))
        filtered (filter reducer-fn keys)
        latest (first filtered)]
    latest
    {:name dep-group-and-name :version (latest 1)}))

(defn get-dependencies []
  (let [current-deps (read-dependencies)
        latest-deps (pmap find-latest-version current-deps)]
    (for [current current-deps
          latest latest-deps
          :when (= (:name current) (:name latest))
          :when (not= (:version current) (:version latest))]
      {:name (:name current)
       :current-version (:version current)
       :latest-version (:version latest)})))

(defn run
  [project-file]
  (binding
      [*project* (lein.project/read project-file)]
    (get-dependencies)))
