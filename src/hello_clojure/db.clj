(ns hello-clojure.db
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [clojure.java.jdbc :as jdbc]
            [jdbc.pool.c3p0 :as pool]))

(def ^:private spec
  (pool/make-datasource-spec
    {:subprotocol "postgresql"
     :initial-pool-size 3
     :user "helloclojure"
     :password "boring_unsafe_pw"
     :subname "//localhost:5432/helloclojure"}))


(defn getTime []
  (get (first (jdbc/query spec ["select localtimestamp"]))
       :localtimestamp))


(defn getSum
  "Bro, do you even add?"
  [x y]
  (second (first (first (jdbc/query spec ["select ? + ?" x y])))))
