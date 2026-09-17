(ns util)

(defmacro where [body bindings]
  `(let ~bindings
     ~body))

(defmacro wherefn [body bindings]
  `(letfn ~bindings
     ~body))
