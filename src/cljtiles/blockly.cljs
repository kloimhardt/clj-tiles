(ns cljtiles.blockly
  (:require
   ["blockly" :as blockly]

   [goog.dom :as gdom]
   ))

(def dev true)
(if dev
  (def menu false)
  (def menu false))

(defonce load? (atom true))

(defn init [startfun]
  (when @load?
    (reset! load? false)
    (let [workspace-item
          #js {:displayText "Run Workspace"
               :preconditionFn (fn [_scope] "enabled")
               :callback (fn [scope] (startfun scope))
               :scopeType (.. blockly -ContextMenuRegistry -ScopeType -WORKSPACE)
               :id "run_workspace"
               :weight 100}
          block-item
          #js {:displayText "Inspect"
               :preconditionFn (fn [_scope] "enabled")
               :callback (fn [scope] (startfun scope))
               :scopeType (.. blockly -ContextMenuRegistry -ScopeType -BLOCK)
               :id "inspect"
               :weight 100}]
      (do
        (js/initblocks blockly)
        (.inject blockly
                 "blocklyDiv"
                 (clj->js (merge {:scrollbars true
                                  :media "/blockly/media/"}
                                 (when menu {:toolbox (gdom/getElement "toolbox")}))))
        (.. blockly -ContextMenuRegistry -registry (register workspace-item))
        (.. blockly -ContextMenuRegistry -registry (register block-item))
        ))))
