(ns cljtiles.utils
  (:require [clojure.string :as str]
            [clojure.walk :as w]
            [cljtiles.xmlparse-2 :as edn->code]
            [reagent.core :as rc]
            [tubax.core :as sax]
            [zprint.core :as zp]))

(defn find-biggest-in-str [sol puzz]
  ;; sucht grössten gemeinsamen String via LCSubStr
  (some->> (js/LCSubStr sol puzz)
       (re-seq #"(\((.+)\)|\[(.+)\]|\{(.+)\})")
       (map first)
       (group-by count)
       (into (sorted-map))
       last
       val
       first))

(defn stringsearch [ss s]
  (if (seq s)
    (->> (map #(subs ss % (+ % (count s))) (range))
         (take-while seq)
         (take-while #(not= s %))
         count)
    (count ss)))

(defn twosplit-sfn [ss s searchfun]
  (cond
    (nil? ss) [""]
    (nil? s) [ss]
    (not (seq s)) [ss]
    :else
    (let [posi (searchfun ss s)]
      (if (= posi (count ss))
        [ss]
        [(subs ss 0 posi) (subs ss (+ posi (count s)))]))))

(defn twosplit [ss s] (twosplit-sfn ss s stringsearch))

(defn fullsplit-no-tail-recursion [ss s]
  (let [[eins zwo] (twosplit ss s)]
    (if zwo
      (cons eins (fullsplit-no-tail-recursion zwo s))
      (list eins))))

(defn fullsplit-tail-recur [ss s]
  (loop [result []
         [eins zwo] (twosplit ss s)]
    (let [new-result (conj result eins)]
      (if zwo (recur new-result (twosplit zwo s)) new-result))))

(defn fullsplit [ss s]
  (->> [[] (twosplit ss s)]
       (iterate (fn [[result [eins zwo]]]
                  [(conj result eins) (twosplit zwo s)]))
       (drop-while (fn [[_ [_ zwo]]] zwo))
       first
       (apply concat)))

(defn twosplit-word [ss s]
  ;; splittet nur wenn der String s als Wort gefunden wird
  (twosplit-sfn ss s
                (fn [ss s]
                  (if (re-find (re-pattern (str "\\b" s "\\b")) ss)
                    (stringsearch ss s)
                    (count ss)))))

(comment
  (defn uuv [a b]
    [(str/split a b) (twosplit a b)])

  (uuv "a" nil)
  (uuv nil nil)
  (uuv nil "a")
  (uuv "ab" "b") ;; => [["a"] ["a" ""]] case it differs
  (uuv "w- " " ")
  (uuv "a" "b")
  (uuv "ba" "b")
  (uuv "" "a")
  (uuv "a" "")
  (uuv "" "") ;; here it differs as well
  (uuv "(a)" "(a)")
  :end)

;; need to be all the same atringlength
(def white "-w")
(def green "-g")
(def black "-b")
(def yellow "-y")
(def clear "-c")
(def col-postfix-len 2)
(assert (= (count clear) (count green) (count white) (count black) (count yellow) col-postfix-len))

(defn get-color-type [s]
  (subs s (- (count s) col-postfix-len)))

(defn is-color? [s c]
  (= (get-color-type s) c))

(defn get-color-data [s]
  (subs s 0 (- (count s) col-postfix-len) ))

(defn make-color [s c]
  (str s c))

(defn convert-to-sorted [puzzd-list]
  (->> puzzd-list
       (w/prewalk (fn [x]
                    (cond
                      (map? x) (into (sorted-map) x)
                      (set? x) (into (sorted-set) x)
                      :else x)))))

(defn make-coll-green [xs]
  (cond->> (map (fn [x]
                  (if (map-entry? x)
                    [(make-color (str (key x)) green)
                     (make-color (str (val x)) green)]
                    (make-color (str x) green))) xs)
    (some identity ((juxt map? vector? set?) xs)) (into (empty xs))))

(defn convert-to-color [puzzd-list tree-seq-sold]
  (->> puzzd-list
       (w/prewalk (fn [x]
                    (let [s (pr-str x)
                          jx (juxt identity map? vector? set?)
                          found (some #{(jx x)} (map jx tree-seq-sold))]
                      (cond
                        (map-entry? x) x
                        (coll? x)
                        (if found
                          (make-coll-green x)
                          x)
                        (and (string? x) (is-color? x green)) x
                        found (make-color s yellow)
                        :else (make-color s black)))))))

(defn element-convert-parens [elem]
  (let [open-close-char (fn  [coll]
                          (cond
                            (map? coll) ["{" "}" (apply concat (seq coll))]
                            (vector? coll) ["[" "]" (seq coll)]
                            (set? coll) ["#{" "}" (seq coll)]
                            :else ["(" ")" (seq coll)]))
        [open close leaves] (open-close-char elem)]
    (concat [(make-color open white)]
            (interpose (make-color " " white) leaves)
            [(make-color close white)])))

(defn convert-parens-to-strings [edn-list]
  (->> edn-list
       (map #(->> % (w/prewalk (fn [x]
                                 (if (coll? x)
                                   (element-convert-parens x) x)))))
       (interpose (make-color " " white))
       flatten))

(defn expand-greens [strings]
  (->> strings
       (mapcat (fn [s] (if (is-color? s green)
                         (->> (str/split (get-color-data s) #" ")
                              (map #(make-color % green))
                              (interpose (make-color " " green)))
                         [s])))))

(comment
  (expand-greens ["g-(1 2 3)" "y-4" "g-(5)"])
;;= ("g-(1" "g- " "g-2" "g- " "g-3)" "y-4" "g-(5)")
  :end)

(defn replace-blanks-with-newline [code colorwords]
  (->> colorwords
       (remove #{white (make-color " " white)})
       (partition-all 2 1)
       (reduce (fn [[shrinking-code new-colorwords] tuple]
                 (let [[firststr secondstr] (map #(get-color-data %) tuple)
                       firstpos (+ (stringsearch shrinking-code firststr)
                                   (count firststr))
                       secondpos (+ (stringsearch (subs shrinking-code firstpos) secondstr)
                                    firstpos)
                       blanks (make-color (subs shrinking-code firstpos secondpos) clear)]
                   (if (= firstpos (+ (count shrinking-code) (count firststr))) ;;not found ->brute
                     (do (println "error: coloring stopped as tuple not found " tuple)
                         (reduced [shrinking-code (conj new-colorwords blanks)])) ;;exit, ->
                     ;;results in rest not being colored as here the code is in 'blanks' variable
                     [(subs shrinking-code secondpos) (conj new-colorwords (first tuple) blanks)])))
               [code []])
       peek))

(defn generate-hiccup [strings]
  (letfn [(fgen [s bgc c] [:span {:style {:background-color bgc
                                          :color c}}
                           s])
          (fgreen [s] (fgen s "green" "white"))
          (fwhite [s] (fgen s "white" "black"))
          (fyellow [s] (fgen s "LightYellow" "black"))
          (fblack [s] (fgen s "black" "white"))
          (fclear [s] [:span s])
          (col-dispatch [s] ((get {green  fgreen
                                   white  fwhite
                                   yellow  fyellow
                                   black  fblack
                                   clear fclear}
                                  (get-color-type s))
                             (get-color-data s)))]
    (map col-dispatch strings)))

(defn replace-first-green-blank [xs]
  (let [greenblank? #(and (is-color? % green) (= (get-color-data %) " "))]
    (->> xs
         (remove #{clear})
         (partition 2 1 xs)
         (map (fn [[g c]] (if (and (greenblank? g) (is-color? c clear))
                            (make-color " " clear)
                            g))))))

(def output-width 41)

(defn code-break-primitive [edn-code]
  (map #(zp/zprint-str % output-width) edn-code))

(defn code->break-str [edn-code]
  (apply str (interpose "\n" (code-break-primitive edn-code))))

(defn get-edn-code-simpl [xml-str]
  (edn->code/parse (sax/xml->clj xml-str) nil))

(defn error-boundary [_comp reset-state-fn set-state-fn kw val]
  (let [error (rc/atom nil)]
    (rc/create-class
     {:component-did-catch (fn [_this _e _info] nil)
      :get-derived-state-from-error (fn [e]
                                      (reset! error e)
                                      #js {})
      :reagent-render (fn [comp]
                        (if @error
                          (do
                            (reset-state-fn nil)
                            (set-state-fn kw val)
                            (reset! error nil)
                            nil)
                          comp))})))

(defn render-colored-comp [edn-code edn-sol]
  (let [tree-seq-solution (tree-seq coll? seq edn-sol)
        massaged-puzzle (convert-to-sorted edn-code)]
    (->> (convert-to-color massaged-puzzle tree-seq-solution)
         (convert-parens-to-strings)
         (expand-greens)
         (replace-blanks-with-newline (code->break-str massaged-puzzle))
         (replace-first-green-blank)
         (generate-hiccup)
         (into [:p {:style {:display "block" :font-family "monospace"
                            :white-space "pre" :margin ["1em" 0]}}]))))

(defn render-colored [code edn-code xml-sol xml-puzzle
                      state-colored-code tutorial-number
                      solved-tutorials
                      fn-update-state-field]
  ;;puzz is always a vector of code: => ["Hello, World!"]
  ;;(def puzz puzz)
  ;;(def xml-sol xml-sol)
  (let [show-color-button (and fn-update-state-field xml-sol
                               (contains? solved-tutorials (dec tutorial-number)))]
    [:div
     [:h3 "Code "
      (when show-color-button
        (letfn [(set-state-field [kw val] (fn-update-state-field kw (constantly val)))]
          [:button {:on-mouse-down #(set-state-field :colored-code true)
                    :on-mouse-up #(set-state-field :colored-code false)
                    :on-mouse-leave #(set-state-field :colored-code false)}
           "Color"]))]
     (if (and state-colored-code show-color-button)

       (letfn [(set-state-field [kw val] (fn-update-state-field kw (constantly val)))]
         (let [edn-sol (get-edn-code-simpl xml-sol)
               edn-puzz (get-edn-code-simpl xml-puzzle)
               _ (if (= edn-sol edn-puzz)
                   (do
                     (fn-update-state-field :solved-tutorials #(conj % tutorial-number))
                     (set-state-field :forward-button-green true))
                   (set-state-field :forward-button-green false))]
           [error-boundary
            [render-colored-comp edn-code edn-sol]
            identity set-state-field :colored-code false]))
       [:pre code])]))
