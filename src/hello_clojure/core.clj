(ns hello-clojure.core
  (:use compojure.core)
  (:require [hello-clojure.db :as db]
            [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as response]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(defn getDBTime []
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str {:request {:operation "time"}
                          :response {:localtimestamp (str (get (first (jdbc/query db/spec ["select localtimestamp"]))
                                                               :localtimestamp))}})})

(defn getDBSum [x y]
  {:status 200
      :headers {"Content-Type" "application/json"}
      :body (json/write-str {:request {:operation "sum" :first_operand x :second_operand y}
                             :response {:sum (second (first (first (jdbc/query db/spec ["select ? + ?" x y]))))}})})

(def apiInfoView "
<html>
  <head>
  <title>Hello Clojure</title>
  </head>
  <body>
     <p>Welcome to the app.</p>
     <p>Go GET <a href='/time'>/time</a></p>
     <p>Or go GET <a href='/sum-2-int/42/58'>/sum-2-int/42/58</a></p>
  </body
</html>
")

(defroutes app-routes
  (GET "/" [] apiInfoView)
  (GET "/time" [] (getDBTime))
  (GET "/sum-2-int/:x/:y" [x y] (getDBSum (Integer/parseInt  x) (Integer/parseInt  y)))
  (route/resources "/")
  (route/not-found "404: Not Found"))

(def app
  (handler/site app-routes))

(defn server []
  (run-jetty app {:join? false, :port 8082}))

(defn -main [& args]
  (server))
