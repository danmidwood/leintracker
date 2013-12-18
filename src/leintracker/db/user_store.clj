(ns leintracker.db.user-store
  (:require [leintracker.db.db :as db]
            [taoensso.timbre :as log]
            [korma.core :refer [select where insert values] :as kc]))


;;;;;; Database
(kc/defentity user)

(defn read [id]
  (select user
          (where {:id id})))

(defn write [this-user]
  (insert user
          (values this-user)))

(defn delete [id]
  (kc/delete user
          (where {:id id})))
