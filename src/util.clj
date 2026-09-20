(ns util
  (:require [clojure.pprint :as pprint]))

(defmacro where [body bindings]
  `(let ~bindings ~body))

(defmacro wherefn [body bindings]
  `(letfn ~bindings
     ~body))

