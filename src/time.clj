(ns time)
(def sqlite-datetime-fmt
  (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss"))

(def sqlite-date-fmt
  (DateTimeFormatter/ofPattern "yyyy-MM-dd"))

(defn now [format]
  (.format (LocalDateTime/now) format))

(defn rel-now [format offset]
  (.format (.plusDays (LocalDateTime/now) offset) format))
