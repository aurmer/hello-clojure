(ns hello-clojure.core
  (:use compojure.core)
  (:require [hello-clojure.db :as db]
            [clojure.data.json :as json]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as response]
            [ring.middleware.defaults :refer :all]
            [compojure.route :as route]))

(defn wrap-dir-index [handler]
  (fn [req]
    (handler
      (update-in req [:uri]
                 #(if (= "/" %) "/index.html" %)))))

(defn getDBTime []
  {:status 200
   :headers {"Content-Type" "application/json"
             "Access-Control-Allow-Origin" "*"}
   :body (json/write-str {:request {:operation "time"}
                          :response {:localtimestamp (str (db/getTime))}})})

(defn getDBSum [x y]
  {:status 200
      :headers {"Content-Type" "application/json"
                "Access-Control-Allow-Origin" "*"}
      :body (json/write-str {:request {:operation "sum" :first_operand x :second_operand y}
                             :response {:sum (db/getSum x y)}})})

(defroutes app-routes
  (GET "/api/time" [] (getDBTime))
  (GET "/api/sum-2-int/:x/:y" [x y] (getDBSum (Integer. x) (Integer. y)))
  (route/resources "/")
  (route/not-found "404: Not Found"))

(def app
  (-> (wrap-defaults app-routes site-defaults)
      wrap-dir-index))

(defn server []
  (run-jetty app {:join? false, :port 8082}))

(defn -main [& args]
  (server))
