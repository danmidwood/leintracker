(ns leintracker.core
  (:require [leintracker.external.lein :as lein]
            [leintracker.external.github :as github]
            [leintracker.repo-store :as db]
            [clojure.java.io :as io]
            [taoensso.timbre :as log]
            [clojure.core.reducers :as r]))

;;;; Home page
(defn home-page [identity]
  (when identity
    {:user (github/user-name identity)}))


;;;; Repos page
(defn find-dependencies [repo]
  (do
    (log/debug (format "Finding dependencies for %s" (:full-name repo)))
    (let
        [file (java.io.File/createTempFile (.replace (:full-name repo) "/" "-")
                                           ".github")]
      (with-open [reader (github/get-lein-file-reader repo)
                  writer (io/writer file)]
        (io/copy reader writer)
        (.deleteOnExit file))
      (lein/run (.getAbsolutePath file)))))

(defn ^:private add-dependencies-to-database [db repo]
  (if (get (:full-name repo) db)
    db
    (assoc db (:full-name repo) (find-dependencies repo))))

(defn get-dependencies [repo]
  (db/get-dependencies repo))

(defn ^:private add-dependencies [repo]
  (if (:is-lein? repo)
    (assoc repo :dependencies (get-dependencies repo))
    repo))

(defn ^:private get-repos [identity]
  "Load up the repos for the user, first trying a local cache, and then falling back to GitHub"
  (if (db/have-repos? identity)
    (db/lookup-repos identity)
    (let [repos (github/repos identity)]
      (db/store-repos identity repos)
      repos)))



(defn ^:private add-is-lein-project [identity repo]
  (let [augmented-repo (assoc repo :is-lein? (github/file-exists? identity "/project.clj" repo))]
    (log/info :debug (str "Is lein project? " (:full-name repo) " " (:is-lein? augmented-repo)))
    augmented-repo))

(defn ^:private add-dependency-to-repo [repo]
  (assoc repo :dependencies (find-dependencies repo)))

(defn ^:private store-repos [identity repos]
  (db/store-repos identity repos)
  repos)

(defn ^:private load-repos [identity]
  (->> (get-repos identity)
       (pmap (partial add-is-lein-project identity))
       (pmap #(assoc % :tracked :tracked))
       (store-repos identity)))

(defn repos-page [identity]
  (when (log/spy :debug "Loading repos for" identity)
    {:user (log/spy (github/user-name identity))
     :repos (->> (load-repos identity)
                 (pmap add-dependencies))}))

(defn repos [identity]
  (when (log/spy :debug "Loading repos for" identity)
    {:user (log/spy (github/user-name identity))
     :repos (load-repos identity)}))
