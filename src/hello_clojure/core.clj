(ns hello-clojure.core
  (:use compojure.core)
  (:require [hello-clojure.db :as db]
            [clojure.java.jdbc :as jdbc]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as response]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(defn getDBTime []
  {:status 200
      :headers {"Content-Type" "text/plain"}
      :body (str "DB localtimestamp: " (get (first (jdbc/query db/spec ["select localtimestamp"]))
                                            :localtimestamp))})

(defroutes app-routes
  (GET "/" [] (getDBTime))
  (route/resources "/")
  (route/not-found "404: Not Found"))

(def app
  (handler/site app-routes))

(defn server []
  (run-jetty app {:join? false, :port 8082}))

(defn -main [& args]
  (server))
