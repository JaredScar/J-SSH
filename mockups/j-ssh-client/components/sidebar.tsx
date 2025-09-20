"use client"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Server, Play, Zap, Terminal, Settings, Moon, Sun } from "lucide-react"
import type { TabType } from "./ssh-client-layout"
import { useTheme } from "next-themes"

interface SidebarProps {
  activeTab: TabType
  onTabChange: (tab: TabType) => void
  hasActiveSession: boolean
}

export function Sidebar({ activeTab, onTabChange, hasActiveSession }: SidebarProps) {
  const { theme, setTheme } = useTheme()

  const navItems = [
    {
      id: "sessions" as TabType,
      label: "Sessions",
      icon: Server,
      description: "Manage SSH connections",
    },
    {
      id: "actions" as TabType,
      label: "Actions",
      icon: Play,
      description: "Reusable commands",
    },
    {
      id: "triggers" as TabType,
      label: "Triggers",
      icon: Zap,
      description: "Automated sequences",
    },
    {
      id: "terminal" as TabType,
      label: "Terminal",
      icon: Terminal,
      description: "Active session",
      disabled: !hasActiveSession,
    },
  ]

  return (
    <div className="w-64 bg-sidebar border-r border-sidebar-border flex flex-col">
      {/* Header */}
      <div className="p-6 border-b border-sidebar-border">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-sidebar-primary rounded-lg flex items-center justify-center">
            <Terminal className="w-4 h-4 text-sidebar-primary-foreground" />
          </div>
          <div>
            <h1 className="font-semibold text-sidebar-foreground">J-SSH</h1>
            <p className="text-xs text-muted-foreground">SSH Client</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 p-4 space-y-2">
        {navItems.map((item) => {
          const Icon = item.icon
          const isActive = activeTab === item.id
          const isDisabled = item.disabled

          return (
            <Button
              key={item.id}
              variant={isActive ? "default" : "ghost"}
              className={cn(
                "w-full justify-start gap-3 h-12",
                isActive && "bg-sidebar-primary text-sidebar-primary-foreground",
                isDisabled && "opacity-50 cursor-not-allowed",
              )}
              onClick={() => !isDisabled && onTabChange(item.id)}
              disabled={isDisabled}
            >
              <Icon className="w-4 h-4" />
              <div className="text-left">
                <div className="font-medium">{item.label}</div>
                <div className="text-xs opacity-70">{item.description}</div>
              </div>
            </Button>
          )
        })}
      </nav>

      {/* Footer */}
      <div className="p-4 border-t border-sidebar-border space-y-2">
        <Button
          variant="ghost"
          size="sm"
          className="w-full justify-start gap-3"
          onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
        >
          {theme === "dark" ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
          Toggle theme
        </Button>
        <Button variant="ghost" size="sm" className="w-full justify-start gap-3">
          <Settings className="w-4 h-4" />
          Settings
        </Button>
      </div>
    </div>
  )
}
