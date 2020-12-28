(ns cljtiles.view
  (:require
   [goog.dom :as gdom]
   [goog.string :as gstring]
   [goog.dom.forms :as gforms]
   [sci.core :as sci]
   ["blockly" :as blockly]
   [cljtiles.xmlparse :as edn->code]
   [cljtiles.tutorials-0 :as t-0]
   [cljtiles.tutorials-sicm :as t-s]
   [clojure.walk :as w]
   [tubax.core :as sax]
   [reagent.core :as rc]
   [reagent.dom :as rd]
   [zprint.core :as zp]
   [cljtiles.tests :as tst]
   [cljtiles.sicm :as sicm]))

(def dev true)
(if dev
  (do
    (def menu false)
    (print (tst/test-pure)))
  (def menu false))

(def tutorials (vec (concat t-s/vect t-0/vect)))

(def chapnames [" I" "II" "III" "IV" " V" "VI"])
(def chaps [11 6 14 9 9 1])
(def rocket-no 49)

(defn page->chapter [page-no]
   (- (count chaps) (count (filter #(> % page-no) (reductions + chaps)))))

(defn chapter->page [chap-no]
  (if (pos? chap-no) (nth (reductions + chaps) (dec chap-no)) 0))

(defn load-workspace [xml-text]
  (.. blockly/Xml
      (clearWorkspaceAndLoadFromXml (.. blockly/Xml (textToDom xml-text))
                                    (.getMainWorkspace blockly))))

(defonce state (rc/atom nil))

(defonce app-state (rc/atom nil))

(defn goto-page! [page-no]
  (load-workspace (get tutorials page-no))
  (gforms/setValue (gdom/getElement "tutorial_no") page-no)
  (reset! state
          {:stdout nil :result nil :code nil :tutorial-no page-no})
  (reset! app-state 0))

(defn tutorial-fu [inc-or-dec]
  (fn []
    (let [el (gdom/getElement "tutorial_no")
          idx-old  (gstring/toNumber (gforms/getValue el))
          idx-new (inc-or-dec idx-old)
          idx (cond
                (< -1 idx-new (count tutorials)) idx-new
                (> 0 idx-new) 0
                (< (dec (count tutorials)) idx-new) (dec (count tutorials))
                (< -1 idx-old (count tutorials)) idx-old
                :else 0)]
      (goto-page! idx))))

(defonce workspace
  (do
    (js/initblocks blockly)
    (.inject blockly
             "blocklyDiv"
             (clj->js (merge {:scrollbars true
                              :media "/blockly/media/"}
                             (when menu {:toolbox (gdom/getElement "toolbox")}))))))

((tutorial-fu identity))

(def thexml (atom ""))

(defn code->break-str [width edn-code]
  (if-let [code  (:dat edn-code)]
    (apply str (interpose "\n" (map #(zp/zprint-str % width) code)))
    (zp/zprint-str edn-code width)))

(defn code->break-str1 [width edn-code]
  (apply str (interpose "\n" (map #(zp/zprint-str % width) edn-code)))
  )

(defn part-str [width s]
  (apply str
         (interpose "\n"
                    (map (partial apply str)
                         (partition-all width s)))))

(defn my-str [e width]
  (let [f (fn [x]
            (if (nil? x) "nil" (str x)))]
    (if (seq? e)
      (part-str width (apply str (interpose " " (map f e))))
      (part-str width (f e)))))

(defn augment-code-fu [edn-code flat-code fn-code]
  (if (and (seq (filter #{(second fn-code)} flat-code))
           (:code edn-code))
    (if (:dat (:code edn-code))
      (update-in edn-code [:code :dat] #(cons fn-code %))
      (update edn-code :code (fn [c] {:dat [fn-code c]})))
    edn-code))

(defn augment-code-fu1 [edn-code flat-code fn-code]
  (if (seq (filter #{(second fn-code)} flat-code))
    (into [] (cons fn-code edn-code))
    edn-code))

(defn augment-code [edn-code]
  (let [flat-code (flatten (w/postwalk #(if (map? %) (vec %) %) edn-code))]
    (-> edn-code
        (augment-code-fu flat-code
                         '(defn vec-rest "added by Blockly parser" [x]
                            (let [r (rest x)] (if (seq? r) (vec r) r))))
        (augment-code-fu flat-code
                         '(defn vec-cons "added by Blockly parser" [x coll]
                            (let [c (cons x coll)] (if (seq? c) (vec c) c)))))))

(defn augment-code1 [edn-code]
  (let [flat-code (flatten (w/postwalk #(if (map? %) (vec %) %) edn-code))]
    (-> edn-code
        (augment-code-fu1 flat-code
                         '(defn vec-rest "added by Blockly parser" [x]
                            (let [r (rest x)] (if (seq? r) (vec r) r))))
        (augment-code-fu1 flat-code
                         '(defn vec-cons "added by Blockly parser" [x coll]
                            (let [c (cons x coll)] (if (seq? c) (vec c) c)))))))

(def timer (atom nil))
(def counter (atom 0))

(defn stop-timer [msg]
  (js/clearInterval @timer)
  (reset! timer nil)
  (reset! counter 0)
  msg)

(defn start-timer [fu ms max msg]
  (when-not @timer
    (reset! timer
            (js/setInterval (fn []
                              (swap! counter inc)
                              (if (< @counter max)
                                (fu)
                                (stop-timer nil))) ms))
    msg))

(defn bindings1 [new-println new-print]
  (merge
    sicm/bindings
    {'println new-println
     'print new-print
     'app-state app-state
     'start-timer start-timer
     'stop-timer stop-timer
     }))

(defn run-code [edn-code &[edn-code1 error]]
  (let [aug-edn-code (augment-code edn-code)
        _ (def j edn-code)
        _ (def k edn-code1)
        _ (def h aug-edn-code)
        aug-edn-code1 (augment-code1 edn-code1)
        _ (def i aug-edn-code1)
        theout (atom "")
        str-width 41
        bindings (merge
                   sicm/bindings
                   {'println (fn [& x]
                                    (swap! theout str (my-str x str-width) "\n") nil)
                         'print (fn [& x]
                                  (swap! theout str (my-str x str-width)) nil)
                         'app-state app-state
                         'start-timer start-timer
                         'stop-timer stop-timer
                    })
        new-println (fn [& x]
                      (swap! theout str (my-str x str-width) "\n") nil)
        new-print (fn [& x]
                    (swap! theout str (my-str x str-width)) nil)

        bindings2 (bindings1 new-println new-print)
        cbr (code->break-str str-width (:code aug-edn-code))
        _ (def e cbr)
        cbr1 (code->break-str1 str-width aug-edn-code1)
        _ (def f cbr1)
        erg (try (sci/eval-string cbr {:bindings bindings})
                 (catch js/Error e (.-message e)))
        _ (def c erg)
        erg1 (try (sci/eval-string cbr1 {:bindings bindings2})
                  (catch js/Error e (.-message e)))
        _ (def d erg1)]
    (when dev
      ;;(println "edn: " edn)
      (println "-------")
      (print (code->break-str str-width (:code aug-edn-code)))
      (println cbr1)
      (println (:error aug-edn-code))
      (println error)
      (println "-------")
      (when @theout (println @theout))
      (println erg)
      (println erg1))
    (swap! state assoc
           :stdout @theout
           :result
           (cond (some? erg) (my-str erg str-width)
                 (= "nil" (str (last (get-in edn-code [:code :dat])))) "nil"
                 :else "")
           :result1
           (cond (some? erg1) (my-str erg1 str-width)
                 (= "nil" (str (last edn-code1))) "nil"
                 :else "")
           :code (if (:error aug-edn-code)
                   "Cannot even parse the blocks"
                   cbr)
           :code1 (if error
                    "Cannot even parse the blocks"
                    cbr1)
           :edn-code (:code aug-edn-code)
           :edn-code1 aug-edn-code1)))

(defn ^:export startsci []
  (let [xml-str (->> (.-mainWorkspace blockly)
                     (.workspaceToDom blockly/Xml)
                     (.domToPrettyText blockly/Xml))
        edn-xml (sax/xml->clj xml-str)
        edn-code (if (seq (:content edn-xml))
                   (try {:code (edn->code/parse edn-xml)}
                        (catch js/Error e {:error (.-message e)})) "")
        {:keys [code error]}
        (when (seq (:content edn-xml))
          (try {:code (edn->code/parse edn-xml)}
               (catch js/Error e {:error (.-message e)})))
        edn-code1 (when code (or (:dat code) [code]))
        _ (def a edn-code)
        _ (def b edn-code1)
        ]
    (reset! thexml xml-str)
    (run-code edn-code edn-code1 error)))

(defn tutorials-comp []
  [:div
   [:span
    [:select {:value (page->chapter (:tutorial-no @state))
              :on-change (fn [el]
                           (let [chap (gstring/toNumber
                                        (.. el -target -value))
                                 no (chapter->page chap)]
                             (goto-page! no)))}
     (map-indexed (fn [idx val] [:option {:key idx :value idx} val]) chapnames)]
    " "
    [:button {:on-click (tutorial-fu dec)} "<"]
    " "
    [:input {:read-only true :size (inc (* 2 (count (str (count tutorials)))))
             :value (str (inc (:tutorial-no @state)) "/" (count tutorials))}]
    " "
    [:button {:on-click (tutorial-fu inc)} ">"]
    " "
    (if (and (not dev) (= (:tutorial-no @state) 0))
      [:span
       [:button {:on-click startsci} "Run this Hello World example"]
       " "
       [:button {:on-click #(goto-page! rocket-no)}
        "Go to Rocket Launch example"]]
      [:button {:on-click startsci} "Run"])]])

(defn filter-defns [edn-code fu]
  (let [ec (if (:dat edn-code) edn-code {:dat [edn-code]})]
    {:dat
     (conj
      (vec (filter #(= (symbol "defn") (first %)) (:dat ec)))
      (list fu)
      (last (:dat ec)))}))

(defn filter-defns1 [edn-code fu]
  (conj
    (vec (filter #(= (symbol "defn") (first %)) edn-code))
    (list fu)
    (last edn-code)))

(defn to-kw [edn-code sy]
  (def p [edn-code sy])
  (cond
    (symbol? sy)
    (let [s (str sy)]
      (cond
        (= ":" (first s)) (keyword (subs s 1 (count s)))
        (= "nil" s) nil
        (= "@app-state" s) @app-state
        :else sy))
    (map? sy)
    (if (:on-click sy)
      (assoc sy
             :on-click
             #(run-code
               {:code
                (filter-defns edn-code (:on-click sy))}))
      sy)
    (list? sy)
    (try (sci/eval-string (pr-str sy))
         (catch js/Error e (.-message e)))
    :else sy))

(defn to-kw1 [edn-code sy]
  (def o [edn-code sy])
  (cond
    (symbol? sy)
    (let [s (str sy)]
      (cond
        (= ":" (first s)) (keyword (subs s 1 (count s)))
        (= "nil" s) nil
        (= "@app-state" s) @app-state
        :else sy))
    (map? sy)
    (if (:on-click sy)
      (assoc sy
             :on-click
             #(run-code nil
                        (filter-defns1 edn-code (:on-click sy))
                        nil))
      sy)
    (list? sy)
    (try (sci/eval-string (pr-str sy))
         (catch js/Error e (.-message e)))
    :else sy))

(defn transform-vec [vect edn-code]
  (w/postwalk #(to-kw edn-code %)
              vect))

(defn transform-vec1 [vect edn-code]
  (def u [vect edn-code])
  (w/postwalk #(to-kw1 edn-code %)
              vect))

(comment
(= (:edn-code @state) (:edn-code1 @state))

  )

(defn reagent-comp []
  (let [last-vec
        (cond
          (vector? (:edn-code @state)) (:edn-code @state)
          (and (map? (:edn-code @state))
               (vector? (last (:dat (:edn-code @state)))))
          (last (:dat (:edn-code @state)))
          :else [nil])]
    (when (= (symbol ":div") (first last-vec))
      [:div
       (transform-vec last-vec (:edn-code @state))])))
#_(reagent-comp)

(defn reagent-comp1 []
  (let [v? #(when (vector? %) %)
        last-vec (v? (last (:edn-code1 @state)))]
    (when (= (symbol ":div") (first last-vec))
      [:div
       (transform-vec1 last-vec (:edn-code1 @state))])))
#_(reagent-comp1)

(defn out-comp []
  (rc/create-class
   (merge
    {:reagent-render
     (fn []
       [:div
        (when menu
          [:input {:type "text" :value (pr-str @thexml) :id "xmltext"
                   :read-only true}])
        [tutorials-comp]
        [reagent-comp]
        #_[reagent-comp1]
        (when (:result @state)
          (let [showcode? (or dev (not (#{0 1 rocket-no} (:tutorial-no @state))))]
            (when (< (:tutorial-no @state) (dec (count tutorials)))
              [:table {:style {:width "100%"}}
               [:thead
                [:tr {:align :left}
                 [:th {:style {:width "50%"}} "Output"]
                 (when showcode? [:th "Code"])]]
               [:tbody
                [:tr
                 [:td {:align :top}
                  (when-let [so (:stdout @state)]
                    [:pre so])
                  [:pre (:result @state)]]
                 (when showcode?
                   [:td {:align :top} [:pre (:code @state)]])]]])))])}
    (when menu
      {:component-did-update (fn []
                               (.select (gdom/getElement "xmltext"))
                               (.execCommand js/document "copy"))}))))

(defn theview []
  [:div
   [out-comp]])

(defn ^{:export true :dev/after-load true} output []
  (rd/render [theview] (gdom/getElement "out")))
