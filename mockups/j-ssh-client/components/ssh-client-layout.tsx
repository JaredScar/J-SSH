"use client"

import { useState } from "react"
import { Sidebar } from "@/components/sidebar"
import { SessionsTab } from "@/components/sessions-tab"
import { ActionsTab } from "@/components/actions-tab"
import { TriggersTab } from "@/components/triggers-tab"
import { TerminalInterface } from "@/components/terminal-interface"

export type TabType = "sessions" | "actions" | "triggers" | "terminal"

export function SSHClientLayout() {
  const [activeTab, setActiveTab] = useState<TabType>("sessions")
  const [activeSession, setActiveSession] = useState<string | null>(null)

  const renderContent = () => {
    switch (activeTab) {
      case "sessions":
        return <SessionsTab onSessionConnect={setActiveSession} />
      case "actions":
        return <ActionsTab />
      case "triggers":
        return <TriggersTab />
      case "terminal":
        return <TerminalInterface sessionId={activeSession} />
      default:
        return <SessionsTab onSessionConnect={setActiveSession} />
    }
  }

  return (
    <div className="flex h-screen bg-background">
      <Sidebar activeTab={activeTab} onTabChange={setActiveTab} hasActiveSession={!!activeSession} />
      <main className="flex-1 overflow-hidden">{renderContent()}</main>
    </div>
  )
}
