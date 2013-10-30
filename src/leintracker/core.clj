(ns leintracker.core
  (:require [leintracker.external.lein :as lein]
            [leintracker.external.github :as github]
            [clojure.java.io :as io]
            [taoensso.timbre :as log]
            [clojure.core.reducers :as r]))

(defn find-dependencies [repo]
   (do
     (log/debug (format "Finding dependencies for %s" (:full-name repo)))
     (let
         [file (java.io.File/createTempFile "hello" ".github")]
       (with-open [reader (github/get-lein-file-reader repo)
                   writer (io/writer file)]
         (io/copy reader writer))
       (lein/run (.getAbsolutePath file)))))

(defn home-page [identity]
  (when identity
    {:user (github/user-name identity)}))

(defn ^:private is-lein-project? [identity repo]
  (log/spy :debug
           (str "Is lein project? " (:full-name repo))
           (github/file-exists? identity "/project.clj" repo)))

(defn ^:private add-dependency-to-repo [repo]
  (assoc repo :dependencies (find-dependencies repo)))

(defn add-dependencies [repos]
  (map add-dependency-to-repo repos))


(defn repos-page [identity]
  (when (log/spy :debug "Loading repos for" identity)
    {:user (log/spy (github/user-name identity))
     :repos (add-dependencies (into []
                                    (r/filter (partial is-lein-project? identity)
                                              (github/repos identity))))}))
