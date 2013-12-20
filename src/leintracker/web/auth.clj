(ns leintracker.web.auth
  (:use compojure.core)
  (:require [cemerick.friend :as friend]
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :as oauth2-util]
            [ring.util.response :as resp]
            [ring.util.codec :as codec]
            [environ.core :refer [env]]
            [taoensso.timbre :as log]
            [leintracker.external.github :as gh]))

(def ^:private auth-callback-path "/authcb")

(def ^:private client-config
  {:client-id (env :leintracker-gh-client-key)
   :client-secret (env :leintracker-gh-client-secret)
   :callback {:domain (env :leintracker-gh-callback-domain)
              :path auth-callback-path}})

(def ^:private uri-config
  {:authentication-uri {:url (env :leintracker-gh-auth-uri)
                        :query {:client_id (:client-id client-config)
                                :response_type "code"
                                :redirect_uri (oauth2-util/format-config-uri client-config)
                                :scope ""}}
   :access-token-uri {:url (env :leintracker-gh-access-token-uri)
                      :query {:client_id (:client-id client-config)
                              :client_secret (:client-secret client-config)
                              :grant_type "authorization_code"
                              :redirect_uri (oauth2-util/format-config-uri client-config)
                              :code ""}}})

(defn redirect-to-home [req] (resp/redirect (str (:context req) "/")))

(def read-identity friend/identity)

(defroutes github-routes-unsecure
  (GET "/logout" req

       (friend/logout* (redirect-to-home req)))
  (GET "/login" req
       (do
         (log/info "Logging in" req)
         (friend/authenticated
          (redirect-to-home req)))))

(defn ^:private credential-fn [creds]
  (let
      [token (:access-token creds)]
    {:identity token
     :user-name (log/spy (gh/user-name {:current token}))}))

(def workflow (oauth2/workflow
               {:client-config client-config
                :uri-config uri-config
                :config-auth {}
                :credential-fn credential-fn
                :access-token-parsefn oauth2-util/get-access-token-from-params}))

(defroutes auth-routes
  (friend/authenticate
   github-routes-unsecure
   {:allow-anon? true
    :default-landing-uri "/"
    :login-uri auth-callback-path
    :unauthorized-handler #("You do not have sufficient privileges to access " (:uri %))
    :workflows [workflow]}))
