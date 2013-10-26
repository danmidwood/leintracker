(ns leintracker.core
  (:require [leintracker.external.lein :as lein]
            [leintracker.external.github :as github]
            [clojure.java.io :as io]))

(defn find-dependencies [user project]
  (let
      [file (java.io.File/createTempFile project ".github")]
    (with-open [reader (github/get-lein-file-reader user project)
                writer (io/writer file)]
      (io/copy reader writer))
    (lein/run (.getAbsolutePath file))))

(defn home-page [identity]
  (when identity
    {:user (github/get-github-name identity)}))

(defn repos-page [identity]
  (when identity
    {:user (github/get-github-name identity)}))
