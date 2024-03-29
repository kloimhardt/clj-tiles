(ns cljtiles.tutorials-sicm2
  (:require [cljtiles.sicm :as sc]))

(def bold {:style {:font-weight "bold"}})

(def e-vect
  [{:lable :free-particle
    :description
    [:div
     [:div bold "Description"]
     [:p "This is the Free Particle again. You reconize it: it was at the very beginning of the Driven Pendulum chapter. But here, solving the puzzle comes in the form of an interactive journey. The right-click + inspect feature not only delivers the result of the code under scrutiny, it also gives some hint on how to proceed. Note: the journey feature is very experimental and you can get off track quickly. But there is a "
      [:a {:href "https://www.youtube.com/watch?v=DHcZkmXKp04"} "walkthrough video"] "."]
     [:div bold "Interactive tutorial"]
     [:p "Number 5 on the upper left catches your attention. Full of curiosity, you right click and inspect it."]]
    :hint ["(Path-of-a-Free-Particle :tiles/slot) 10"
           "(Path-of-a-Free-Particle :tiles/slot) 't"]
    :error-message-fn
    (fn [{:keys [sci-error edn-code] :as the-state} ifo msg-fn]
      (let [frm (last ifo)
            frmcoll (when (coll? frm) frm)]
        #_(do
            (def brk-2 (sc.api/spy {:sc/cs-label :brk-2} (sc.api/last-ep-id)))
            #_(sc.api/defsc :brk-2))

        (cond
          (= "Var name should be simple symbol." (subs sci-error 0 33))
          "You cannot inspect this block."
          (and (= frm 'time)
               (= (subs sci-error 0 30) "Could not resolve symbol: time")
               (empty? (filter #(= (and (coll? %) (nth % 2 nil)) ['time])
                               edn-code)))
          (msg-fn the-state '(nil time-error) nil)
          (= (subs sci-error 0 35) "Could not resolve symbol: Path-of-a")
          (msg-fn the-state '(nil particle-error) nil)
          (and (= 'defn (first frmcoll)) (= "Parameter declaration missing" (subs sci-error 0 29)))
          "The \"defn\" block has three connections, but only one is actually connected. You notice the \"Path-of-a-Free-Particle\" block. You connect it and inspect the whole \"defn\" block."
          :else
          "No specific explanation is programmed for the below message. This shows that you are exploring. So don't worry, just make another step.")))
    :message-fn
    (fn [{:keys [inspect]} ifo goto-lable-page!]
      (let [frm (last ifo)
            frmcoll (when (coll? frm) frm)
            last-ifo (cond
                       (and (coll? frm) (= (first frm) 'Path-of-a-Free-Particle))
                       ({[true true] 'Path-of-a-Free-Particle-sym-vec
                         [true false] 'Path-of-a-Free-Particle-sym
                         [false true] 'Path-of-a-Free-Particle-num-vec
                         [false false] 'Path-of-a-Free-Particle-num}
                        [(js/isNaN (js/parseInt (last frm)))
                         (= ::sc/up (first (sc/classify (first inspect))))])
                       (= (str frm) "'t")
                       't-symbol
                       (and (coll? frm)
                            (= '(defn Path-of-a-Free-Particle [time])
                               (take 3 frm))
                            (= 'up (#(and (coll? %) (first %)) (nth frm 3 nil))))
                       'Path-of-a-Free-Particle-fn
                       :else frm)]
        #_(do
            (def brk-1 (sc.api/spy {:sc/cs-label :brk-1} (sc.api/last-ep-id)))
          ;;(sc.api/defsc :brk-1)
            )
        (or
         (get
          {:start-interactive
           [:p "This workspace is about modelling the motion of a free particle. You see lots of building blocks. Number 5 on the upper left catches your attention. Full of curiosity, you right click and inspect it."]
           (symbol :5) "Number 5. Next you look at number 4."
           (symbol :4) "You multiply 5 and 4. "
           (list '* (symbol :5) (symbol :4))
           "You wonder if \\((2 + 5 * 4)\\) works."
           (list '+ (symbol :2) (list '* (symbol :5) (symbol :4)))
           "Indeed 22. Next, the block called \"up\" looks interesting."
           '(up)
           "An unknown type. This block does not seem to mean very much. But noticing its two connections, you attach the formula just created.
"
           (list 'up (list '+ (symbol :2) (list '* (symbol :5) (symbol :4))))
           "You read the type of the result: \"Column Vector\". But there are no columns. You connect the number 3."
           (list 'up (list '+ (symbol :2) (list '* (symbol :5) (symbol :4))) (symbol :3))
           [:<>
            [:p "At last a proper column vector in two dimensions."]
            [:p "A block called \"time\" is nearby. Certainly, a time dependent vector would be nice. You inspect \"time\"."]]
           'time-error
           "The code interpreter cannot resolve a symbol. Obviously, the \"time\" block must not be by itself. You connect it to the \"defn\" block and inspect again."
           'particle-error
           "The code interpreter cannot resolve a symbol. Now you notice the \"Path-of-a-Free-Particle\" block. You connect it and inspect the whole \"defn\" block."
           (list 'defn 'Path-of-a-Free-Particle ['time])
           "A cryptic output without any type at all. But you realize from prior experience that you just created a stub for a function definition. The name of the function is \"Path-of-a-Free-Particle\" and its argument is \"time\". You add a block \\( (4 * time )\\) to the last connection of the \"defn\" block."
           (list 'defn 'Path-of-a-Free-Particle ['time]
                 (list '* (symbol :4) 'time))
           "A cryptic output without any type at all. This is expected, as you know that functions need to be called. You right-click on empty space to open the parser and create the call statement (you notice the hint button)."
           'Path-of-a-Free-Particle-num
           "You are pleased to finally get some number. Inspecting \"time\", the argument of the function just called, seems intersting."
           't-symbol
           "A new type: a Symbol. You notice the single quote, allowing t to stand by itself, more akin to a number."
           'Path-of-a-Free-Particle-fn
           "You see some cyptic output. You'd better call the function."
           'Path-of-a-Free-Particle-num-vec
           "A vector of numbers. Calling the function with a symbol is certainly more interesting."
           'Path-of-a-Free-Particle-sym-vec
           "Finally, the time dependent vector! You inspect the argument \"time\" of the function again."}
          last-ifo)
         (when (= frm 'time)
           (let [c (map sc/classify inspect)]
             (if (> (count (into #{} c)) 1)
               [:<>
                [:p (str "The block changes type during the course of the program. It is first a " (last (first c)) ", than a " (last (last c)) ". You notice a button below.")]
                [:div [:button {:on-click (fn [] (goto-lable-page! :pendulum-final nil))} "Make a huge leap"]]]
               (if (= :cljtiles.sicm/nu (first (first c)))
                 [:<>
                   ;;after 'Path-of-a-Free-Particle-num
                  [:p "Time is also a number. But you suspect that it could be something else as well."]
                  [:p "Remembering the hint button, you press it twice after opening the parser."]]

                 (str "The block \"time\" is a " (last (first c)) ". But a block can have more than one type during the course of a program.")))))
         (when (= last-ifo 'Path-of-a-Free-Particle-sym)
           (if (= :cljtiles.sicm/literal-expression (first (sc/classify (first inspect))))
             "This is yet another new type: an Expression. Now you start to finish the free particle motion:
\\[ \\begin{pmatrix}
      x(t) \\\\
      y(t)
    \\end{pmatrix}
=
    \\begin{pmatrix}
      2 + 5t \\\\
      3 + 4t
    \\end{pmatrix}
\\]"
             "An expected result. Certainly the function called is not very complicated."))
         (when (= (take 3 frmcoll) '(defn time [Path-of-a-Free-Particle]))
           [:<>
            [:p "This looks ok at first sight. However, your gut feeling tells you that something is not right. You think of going back to your former exercises about defining functions, albeit you know that you are going to lose your work. Or maybe just permute things?"]
            [:button {:on-click #(goto-lable-page! :defn nil)} "defining functions"]]))))

    :scroll [0 0]
    :blockpos [[0 0] [100 0] [250 0]
               [400 0] [500 0]
               [0 50]
               [300 100]
               [350 110]
               [70 140]
               [0 150]
               [0 250] [150 250]
               [0 300] [150 300]]
    :code [5
           '(:tiles/infix (* :tiles/slot :tiles/slot))
           4
           2
           '(:tiles/infix (+ :tiles/slot :tiles/slot))
           '(:tiles/vert (up :tiles/slot :tiles/slot))
           3
           'time
           'Path-of-a-Free-Particle
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           '(:tiles/infix (* :tiles/slot :tiles/slot))
           'time
           '(:tiles/infix (+ :tiles/slot :tiles/slot))
           'time]
    :puzzle-keep-desc true ;;this is not needed anymore, as :solution was removed
    ;;so the description is displayed anyway.
    ;;It is kept here to remind that this feature exists as it is nowhere used
    }])

(def chapnames ["Free Particle"])
(def chaps [(count e-vect)])

(def content {:tutorials e-vect :chapnames chapnames :chaps chaps})
