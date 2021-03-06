(ns metabase.driver.oracle-test
  "Tests for specific behavior of the Oracle driver."
  (:require [expectations :refer :all]
            [metabase.driver :as driver]
            [metabase.driver
             [generic-sql :as sql]
             oracle])
  (:import metabase.driver.oracle.OracleDriver))

;; make sure we can connect with an SID
(expect
  {:subprotocol "oracle:thin"
   :subname     "@localhost:1521:ORCL"}
  (sql/connection-details->spec (OracleDriver.) {:host "localhost"
                                                 :port 1521
                                                 :sid  "ORCL"}))

;; no SID and not Service Name should throw an exception
(expect
  AssertionError
  (sql/connection-details->spec (OracleDriver.) {:host "localhost"
                                                 :port 1521}))

(expect
  "You must specify the SID and/or the Service Name."
  (try (sql/connection-details->spec (OracleDriver.) {:host "localhost"
                                                      :port 1521})
       (catch Throwable e
         (driver/humanize-connection-error-message (OracleDriver.) (.getMessage e)))))

;; make sure you can specify a Service Name with no SID
(expect
  {:subprotocol "oracle:thin"
   :subname     "@localhost:1521/MyCoolService"}
  (sql/connection-details->spec (OracleDriver.) {:host         "localhost"
                                                 :port         1521
                                                 :service-name "MyCoolService"}))

;; make sure you can specify a Service Name and an SID
(expect
  {:subprotocol "oracle:thin"
   :subname     "@localhost:1521:ORCL/MyCoolService"}
  (sql/connection-details->spec (OracleDriver.) {:host         "localhost"
                                                 :port         1521
                                                 :service-name "MyCoolService"
                                                 :sid          "ORCL"}))
