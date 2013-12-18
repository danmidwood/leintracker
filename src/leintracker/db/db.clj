(ns leintracker.db.db
  (:require [korma.db :as kdb]
            [environ.core :refer [env]]))


;;;;;; Database

(kdb/defdb pg (kdb/postgres
                     {:db (env :leintracker-postgres-db)
                      :user (env :leintracker-postgres-user)
                      :password (env :leintracker-postgres-pass)}))
