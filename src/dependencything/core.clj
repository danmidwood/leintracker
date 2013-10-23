(ns dependencything.core
  (:require [dependencything.lein :as dtlein]
            [dependencything.github :as dtgh]
            [clojure.java.io :as io]))

(defn find-dependencies [user project]
  (let
      [file (java.io.File/createTempFile project ".github")]
    (with-open [reader (dtgh/get-lein-file-reader user project)
                writer (io/writer file)]
      (io/copy reader writer))
    (dtlein/run (.getAbsolutePath file))))

(defn home-page [identity]
  (when identity
    {:user (dtgh/get-github-name identity)}))
