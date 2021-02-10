(ns cljtiles.view
  (:require
   [goog.dom :as gdom]
   [goog.string :as gstring]
   [goog.dom.forms :as gforms]
   [sci.core :as sci]
   [sicmutils.env.sci :as es]
   ["blockly" :as blockly]
   [cljtiles.xmlparse-2 :as edn->code]
   [cljtiles.tutorials-0 :as t-0]
   [cljtiles.genblocks :as gb]
   [cljtiles.tutorials-sicm :as t-s]
   [cljtiles.tutorials-sicm2 :as t-s2]
   [cljtiles.tutorials-sicm3 :as t-s3]
   [cljs.reader :as edn]
   [clojure.walk :as w]
   [tubax.core :as sax]
   [reagent.core :as rc]
   [reagent.dom :as rd]
   [zprint.core :as zp]
   [cljtiles.tests :as tst]
   [cljtiles.sicm :as sicm]
   [cljtiles.blockly :as workspace!]
   [cljtiles.code-analysis :as ca]
   [sc.api]
   [cljtiles.sc]))

(when workspace!/dev
  (print (tst/test-pure)))

(defn generate-xml [page]
  (if (:xml-code page)
    page
    (assoc page :xml-code (apply gb/rpg (:blockpos page) (:code page)))))

(def chaps (concat t-0/chaps
                   t-s2/chaps
                   t-s/chaps
                   t-s3/chaps))

(def chapnames (concat t-0/chapnames
                       t-s2/chapnames
                       t-s/chapnames
                       t-s3/chapnames))

(def tutorials (mapcat #(map generate-xml %)
                 [t-0/e-vect t-s2/e-vect t-s/e-vect t-s3/e-vect]))

(defn page->chapter [page-no]
  (- (count chaps) (count (filter #(> % page-no) (reductions + chaps)))))

(defn chapter->page [chap-no]
  (if (pos? chap-no) (nth (reductions + chaps) (dec chap-no)) 0))

(defn load-workspace [xml-text]
  (.. blockly/Xml
      (clearWorkspaceAndLoadFromXml (.. blockly/Xml (textToDom xml-text))
                                    (.getMainWorkspace blockly))))

(defn append-to-workspace [xml-text]
  (.. blockly/Xml
      (appendDomToWorkspace (.. blockly/Xml (textToDom xml-text))
                            (.getMainWorkspace blockly))))

(defonce state (rc/atom nil))

(defn reset-state [tutorial-no]
  (swap! state merge
          {:stdout [] :inspect [] :sci-error nil
           :result nil
           :code nil
           :edn-code nil
           :edn-code-orig nil
           :reagent-error nil
           :modal-style-display "none"
           :run-button true})
  (when tutorial-no
    (swap! state assoc :tutorial-no tutorial-no)
    (swap! state assoc :desc (:description (nth tutorials tutorial-no)))))

(defonce app-state (rc/atom nil))

(defn set-scrollbar [x y]
  (when x
    (.. blockly -mainWorkspace (scroll x y))))

(defn goto-page! [page-no]
  (load-workspace (:xml-code (nth tutorials page-no)))
  (apply set-scrollbar (:scroll (nth tutorials page-no)))
  (gforms/setValue (gdom/getElement "tutorial_no") page-no)
  (reset-state page-no)
  (reset! app-state 0)
  page-no)

(defn goto-lable-page! [lable]
  (let [cnt (count (take-while false? (map #(= lable (:lable %)) tutorials)))]
    (when (< cnt (count tutorials)) (goto-page! cnt))))

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

(def output-width 41)

(defn code->break-str [edn-code]
  (apply str (interpose "\n" (map #(zp/zprint-str % output-width) edn-code))))

(defn part-str [width s]
  (apply str
         (interpose "\n"
                    (map (partial apply str)
                         (partition-all width s)))))
(defn my-str [x]
  (if (nil? x) "nil" (str x)))

(defn my-str-brk [e]
  (if (seq? e)
    (part-str output-width (apply str (interpose " " (map my-str e))))
    (part-str output-width (my-str e))))

(defn augment-code-fu [edn-code flat-code fn-code]
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

(defn bindings [new-println tex-print tex-inspect]
  (merge
    (es/namespaces 'sicmutils.abstract.function)
    (es/namespaces 'sicmutils.numerical.minimize)
   (es/namespaces 'sicmutils.mechanics.lagrange)
   (es/namespaces 'sicmutils.env)
   {'println new-println
    'tex tex-print
    workspace!/inspect-fn-sym tex-inspect
    'app-state app-state
    'start-timer start-timer
    'stop-timer stop-timer}))

(defn run-code [edn-code error]
  (let [aug-edn-code (augment-code edn-code)
        new-println
        (fn [& x] (swap! state #(update % :stdout conj (apply str x))) nil)
        tex-print
        (fn [& x] (swap! state #(update % :stdout conj
                                        (sicm/tex (last x)))) nil)
        tex-inspect (fn [x] (swap! state #(update % :inspect conj x)) x)
        bindings2 (bindings new-println tex-print tex-inspect)
        cbr (code->break-str aug-edn-code)
        _ (reset-state nil)
        erg (try (sci/eval-string cbr {:bindings bindings2})
                 (catch js/Error e (swap! state assoc :sci-error (.-message e)) nil))
        result (cond (some? erg) (my-str erg)
                     (= "nil" (str (last edn-code))) "nil"
                     :else "")]
    (swap! state assoc
           :result result
           :code (if error "Cannot even parse the blocks" cbr)
           :edn-code aug-edn-code
           :edn-code-orig edn-code)))

(defn insert-inspect [edn-code inspect-form]
    (if inspect-form
      (concat (take 3 edn-code)
              (conj (drop 3 edn-code) inspect-form))
      edn-code))

(defn get-edn-code [xml-str inspect-id inspect-fn]
  (let [edn-xml (sax/xml->clj xml-str)
        eci (edn->code/parse edn-xml {:id inspect-id :fun inspect-fn})
        inspect-fn-name (when inspect-fn (first (inspect-fn 0)))
        ifo (ca/inspect-froms eci inspect-fn-name)]
    (if (not-every? nil? ifo)
      (let [ec (edn->code/parse edn-xml {:id nil :fun nil})]
        (map insert-inspect ec ifo))
      eci)))

(defn ^:export startsci [context]
  (let [xml-str (->> (.-mainWorkspace blockly)
                     (.workspaceToDom blockly/Xml)
                     (.domToPrettyText blockly/Xml))
        edn-code (get-edn-code xml-str
                               (when context (.-id (get context "block")))
                               (:inspect-fn context))]
    (run-code edn-code nil)))

(defn open-modal []
  (swap! state assoc :modal-style-display "block"))

(defn modal-comp [_]
  (let [textarea-element (atom nil)
        parse
        (fn [e]
          (let [a (edn/read-string (str "[" e "]"))
                b (first a)]
            (if (and (map? b) (:code b))
              (:xml-code (generate-xml b))
              (map #(gb/rpg [] %) a))))
        run-parser
        (fn []
          (let [xml (parse (.-value @textarea-element))]
            (if (seq? xml)
              (run! (fn [c] (append-to-workspace c)) xml)
              (load-workspace xml))))
        close-modal
        (fn []
          (set! (.-value @textarea-element) "")
          (swap! state assoc :modal-style-display "none"))]
    (fn [{:keys [tutorial-no modal-style-display]}]
      [:div {:id "myModal", :class "modal"
             :style {:display modal-style-display}}
       [:div {:class "modal-content"}
        [:div
         [:textarea {:cols 80 :rows 10
                     :ref (fn [e] (reset! textarea-element e) (some-> e .focus))}]]
        [:button {:on-click #(do (run-parser) (close-modal))}
         "Insert"]
        " "
        (when-let [h (:hint (nth tutorials tutorial-no))]
          (let [i (atom 0)]
            [:button {:on-click (fn []
                                  (set! (.-value @textarea-element)
                                        (get h @i))
                                  (swap! i inc))}
             "Hint"]))
        [:button {:style {:float "right"} :on-click close-modal}
         "Cancel"]]])))

(defn tutorials-comp [{:keys [run-button tutorial-no]}]
  [:div
   [:span
    [:select {:value (page->chapter tutorial-no)
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
             :value (str (inc tutorial-no) "/" (count tutorials))}]
    " "
    [:button {:on-click (tutorial-fu inc)} ">"]
    " "
    (when run-button
      [:button {:on-click #(startsci nil)} "Run"])]])

(defn filter-defns [edn-code fu]
  (conj
   (vec (filter #(= (symbol "defn") (first %)) edn-code))
   (list fu)
   (last edn-code)))

(defn to-kw [edn-code sy]
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
             #(run-code (filter-defns edn-code (:on-click sy))
                        nil))
      sy)
    (list? sy)
    (try (sci/eval-string (pr-str sy))
         (catch js/Error e (.-message e)))
    :else sy))

(defn transform-vec [vect edn-code]
  (w/postwalk #(to-kw edn-code %)
              vect))

(defn is-last-div [edn-code]
  (let [v? #(when (vector? %) %)
        last-vec (v? (last edn-code))]
    (when (= (symbol ":div") (first last-vec))
      last-vec)))

(defn reagent-comp [last-vec edn-code]
  (transform-vec last-vec edn-code))

(defn tex-comp [txt]
  [:div {:ref (fn [el] (try (.Queue js/MathJax.Hub
                                    #js ["Typeset" (.-Hub js/MathJax) el])
                            (catch js/Error e (println (.-message e)))))}
   txt])

(defn mixed-comp [txt]
  (if (re-find #"(\\\(|\\\[)" txt)
    [tex-comp txt]
    [:pre txt]))

(defn error-comp [{:keys [sci-error code]}]
  (let [flex50 {:style {:flex "50%"}}
        mod-error (if (= (subs (:sci-error @state) 0 40)
                         "Parameter declaration should be a vector")
                    (str "Wrong Parameter declaration" (subs (:sci-error @state) 40))
                    sci-error)]
    [:div {:style {:display "flex"}}
     [:div flex50
      [:h3 "Error"]
      [:pre (my-str-brk mod-error)]]
     [:div flex50
      [:h3 "Code"]
      [:pre code]]]))

(defn result-comp [{:keys [result edn-code edn-code-orig code]}]
  (if (= edn-code edn-code-orig)
    [:pre result]
    (let [flex50 {:style {:flex "50%"}}]
      [:div {:style {:display "flex"}}
       [:div flex50
        [:h3 "Result"]
        [:pre (my-str-brk result)]]
       [:div flex50
        [:h3 "Code"]
        [:pre code]]])))

(defn output-comp [{:keys [edn-code tutorial-no inspect sci-error stdout
                           desc] :as the-state}]
  (if-let [ifo (ca/inspect-form edn-code workspace!/inspect-fn-sym)]
    (let [tut (nth tutorials tutorial-no)]
      [:<>
       (cond
         (seq inspect)
         [:<>
          (map-indexed (fn [idx v]
                         ^{:key idx} [:<>
                                      [mixed-comp (sicm/kind? v)]
                                      [:hr]])
                       inspect)
          (when-let [msg-fn (:message-fn tut)]
            [tex-comp (msg-fn the-state ifo goto-lable-page!)])]
         sci-error
         [:<>
          (when-let [error-msg-fn (:error-message-fn tut)]
            [:p (error-msg-fn the-state ifo (:message-fn tut))])
          [error-comp the-state]]
         (= (last ifo) :start-interactive)
         [tex-comp ((:message-fn tut) the-state ifo goto-lable-page!)]
         )])
    [:<>
     (map-indexed (fn [idx v]
                    ^{:key idx} [mixed-comp v])
                  stdout)
     (when sci-error
       [error-comp the-state])
     (if-let [last-vec (is-last-div edn-code)]
       [reagent-comp last-vec edn-code]
       [result-comp the-state])
     [:div {:style {:column-count 2}}
      [tex-comp desc]]]))

(defn error-boundary [comp]
  (rc/create-class
    {:component-did-catch (fn [this e info] nil)
     :get-derived-state-from-error (fn [e]
                                     (swap! state assoc :reagent-error e)
                                     #js {})
     :reagent-render (fn [comp reagent-error]
                       (if reagent-error
                         [:pre "Something went wrong."]
                         comp))}))

(defn theview []
  [:div
   [modal-comp @state]
   [tutorials-comp @state]
   [error-boundary
    [output-comp @state]
    (:reagent-error @state)]])

(defn ^{:dev/after-load true} render []
  ;;((tutorial-fu identity)) ;;load currenet workspace new
  (rd/render [theview] (gdom/getElement "out")))

(defn ^{:export true} output []
  (workspace!/init startsci open-modal)
  (goto-page! (dec 1))
  (when-let [p (some-> (not-empty (.. js/window -location -search))
                       js/URLSearchParams.
                       (.get "page"))]
    (if (= p "freeparticle")
      (do
        (goto-page! (dec 51))
        (swap! state assoc :run-button false)
        (swap! state assoc :edn-code (list workspace!/inspect-fn-sym :start-interactive)))
      (-> p
          js/parseInt
          dec
          goto-page!)))
  (render))
