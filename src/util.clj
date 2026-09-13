(ns util)

(defmacro where [body & fns]
  `(letfn [~@fns]
     ~body))

(defmacro wherefn [body & fns]
  `(letfn [~@fns]
     ~body))
