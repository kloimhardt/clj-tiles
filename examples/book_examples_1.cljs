(defn test-path
  "See p. 20"
  [t]
  (up (+ (* 4 t) 7)
      (+ (* 3 t) 5)
      (+ (* 2 t) 1)))
(Lagrangian-action (L-free-particle 3)
                   test-path
                   0
                   10)
(defn make-η
  "See p. 21"
  [ν t1 t2]
  (fn [t] (* (- t t1) (- t t2) (ν t))))
(defn varied-free-particle-action
  "See p. 21"
  [mass q ν t1 t2]
  (fn [ε]
    (let [η (make-η ν t1 t2)]
      (Lagrangian-action (L-free-particle
                           mass)
                         (+ q (* ε η))
                         t1
                         t2))))
((varied-free-particle-action
   3
   test-path
   (up sin cos square)
   0
   10)
  0.01)
(minimize (varied-free-particle-action
            3
            test-path
            (up sin cos square)
            0
            10)
          -2
          1)
(defn path-along-x
  [t]
  (up (+ (* 5 t) 1) (* 0 t) (* 0 t)))
(defn make-varied-path
  [ε t0 t1]
  (+ path-along-x
     (* ε
        (make-η (up (fn [x] (* 0 x))
                    identity
                    (fn [x]
                      (* 5 (sin x))))
                t0
                t1))))
(def small-varied-path
  (make-varied-path 0.01 0 10))
(def large-varied-path
  (make-varied-path 0.02 0 10))
[(Lagrangian-action (L-free-particle 3)
                    path-along-x
                    0
                    10)
 (Lagrangian-action (L-free-particle 3)
                    small-varied-path
                    0
                    10)
 (Lagrangian-action (L-free-particle 3)
                    large-varied-path
                    0
                    10)]
