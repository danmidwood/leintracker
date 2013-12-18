(ns leintracker.repo-store
  (:require [leintracker.external.lein :as lein]
            [leintracker.external.github :as github]
            [clojure.java.io :as io]
            [taoensso.timbre :as log]
            [clojure.core.reducers :as r]
            [korma.db :as kdb]
            [korma.core :as kc]
            [environ.core :refer [env]]))


;;;;;; Database

(kdb/defdb pg (kdb/postgres
                     {:db (env :leintracker-postgres-db)
                      :user (env :leintracker-postgres-user)
                      :password (env :leintracker-postgres-pass)}))
(kc/defentity user)
(kc/defentity repo (kc/entity-fields :owner :name :tracked))
(kc/defentity dependencies)


;;;;;; Repos

(def ^:private repo-cache (agent {}))

(defn lookup-repos [{:keys [identity]}]
  (get identity @repo-cache))

(def have-repos? lookup-repos)

(defn ^:private add-repos-to-cache [db identity repos]
  (if (get identity db)
    db
    (assoc db identity repos)))

(defn store-repos [identity repos]
  (send repo-cache add-repos-to-cache identity repos))

;;;;;; Dependencies

(def ^:private dependency-db (agent {}))
(defn exists? [{:keys [full-name]}]
  (get full-name @dependency-db))

(defn get-dependencies [{:keys [full-name]}]
  (get full-name @dependency-db))
