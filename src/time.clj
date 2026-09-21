(ns time
  (:import [java.time LocalDateTime]
           [java.time.format DateTimeFormatter]))

(def datetime-fmt
  (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss"))

(def date-fmt
  (DateTimeFormatter/ofPattern "yyyy-MM-dd"))

(defn now [format]
  (.format (LocalDateTime/now) format))

(defn rel-now [format offset]
  (.format (.plusDays (LocalDateTime/now) offset) format))
