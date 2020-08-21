(ns hello-clojure.db
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [clojure.java.jdbc :as jdbc]
            [jdbc.pool.c3p0 :as pool]))

(def spec
  (pool/make-datasource-spec
    {:subprotocol "postgresql"
     :user "helloclojure"
     :password "boring_unsafe_pw"
     :subname "//localhost:5432/helloclojure"}))
