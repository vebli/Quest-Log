(ns recur
  (:require [malli.core :as m]
            [time :as time]
            [schema.recur :as schema])
  (:import [java.time LocalDateTime DayOfWeek]
           [java.time.temporal TemporalAdjusters WeekFields]))

(defmulti occurrences
  "Returns lazy sequence of occurence dates"
  (fn [rec-type rec-data] rec-type))

(defmethod occurrences :default [_ _] nil)

;; (def ^:private freq->step-fn
;;   {:weekly #(.plusWeeks %1 %2)
;;    :monthly #(.plusMonths %1 %2)
;;    :yearly #(.plusYears %1 %2)})


;; (def ^:private freq->start-of-fn 
;;   {:weekly #(.with % (TemporalAdjusters/previousOrSame
;;                       DayOfWeek/MONDAY))
;;    :monthly #(.withDayOfMonth % 1)
;;    :yearly #(.withDayOfYear % 1)})


;; (defmethod occurrences :periodic
;;   [rec-type {:keys [freq start end interval days] :as rec-data}]
;;   (let [start-of-fn (get freq->start-of-fn freq)
;;         step-fn (get freq->step-fn freq)
;;         start (start-of-fn start)
;;         step (step-fn start interval)]
;;     (when-not (.isAfter step end)
;;       (lazy-seq
;;        (concat
;;         (map #(.plusDays step %) days)
;;         (occurrences rec-type (assoc rec-data :start step)))))))

;; (comment
;;   (def now (LocalDateTime/now))
;;   (def rec-data
;;     {:freq :monthly
;;      :interval 3
;;      :start now
;;      :end (.plusYears now 1)
;;      :days [1 20]
;;      })
;;   (occurrences :periodic rec-data))


