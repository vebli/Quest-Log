(ns util
  (:require [clojure.pprint :as pprint]))

(defmacro where [body bindings]
  `(let ~bindings ~body))

(defmacro wherefn [body bindings]
  `(letfn ~bindings
     ~body))


(defmacro defn-fw
  "Define function and forward declare helpers.
   (defn-fn fname [arg1 ... arg-n]
   :uses [dep-1 ... dep-n]
   body)"
  [name args & forms]
  (let [[key deps & body] forms]
    (assert (= :uses key) "Expected :uses")
    `(do
       (declare ~@deps)
       (defn ~name ~args ~@body))))

