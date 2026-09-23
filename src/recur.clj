(ns recur
  (:require [malli.core :as m]
            [tick.core :as t]
            [util :as util]
            [schema.recur :as schema])
  (:import [java.time DayOfWeek]
           [java.time.temporal TemporalAdjusters WeekFields]))


(defmulti occurrences
  (fn [type data] type))

(defn periodic-seq
  [start end offsets step-fn]
  (->> (iterate step-fn start)
       (take-while #(.isAfter end %))
       (mapcat (fn [period-start]
                 (map #(.plusDays period-start (- % 1))
                      offsets)))
       (drop-while #(.isBefore % start))
       (take-while #(.isAfter end %))))


(defmethod occurrences :weekly
  [_ {:keys [start end offsets gap] :as data}]
  (let [week-start (t/truncate
                    start
                    (TemporalAdjusters/previousOrSame DayOfWeek/MONDAY))
        step-fn #(.plusWeeks % gap)]
    (periodic-seq week-start end offsets step-fn)))

(defmethod occurrences :monthly
  [_ {:keys [start end days gap] :as data}]
  (let [month-start (.withDayOfMonth start 1)
        step-fn #(.plusMonths % gap)]
    (periodic-seq month-start end days step-fn)))

(defmethod occurrences :yearly
  [_ {:keys [start end days gap] :as data}]
  (let [year-start (.withDayOfYear start 1)
        step-fn #(.plusYears % gap)]
    (periodic-seq year-start end days step-fn)))

(comment
  (def now (LocalDateTime/now))
  (.with now (TemporalAdjusters/previousOrSame DayOfWeek/MONDAY))
  (map #(.format % time/date-fmt)
       (take 10
             (periodic-seq
              (.withDayOfMonth (LocalDateTime/now) 1)
              (.plusYears (LocalDateTime/now) 1)
              [1 3]
              #(.plusMonths % 1)))))

