"use client"

import type React from "react"

import { useState, useEffect, useRef } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Terminal, Play, Minimize2, Maximize2, Settings } from "lucide-react"

interface TerminalLine {
  id: string
  type: "command" | "output" | "error"
  content: string
  timestamp: Date
}

interface TerminalInterfaceProps {
  sessionId: string | null
}

export function TerminalInterface({ sessionId }: TerminalInterfaceProps) {
  const [lines, setLines] = useState<TerminalLine[]>([
    {
      id: "1",
      type: "output",
      content: "For a list of supported commands, type 'help'.",
      timestamp: new Date(),
    },
    {
      id: "2",
      type: "output",
      content: "demo@test:/$",
      timestamp: new Date(),
    },
  ])
  const [currentCommand, setCurrentCommand] = useState("")
  const [isConnected, setIsConnected] = useState(!!sessionId)
  const scrollAreaRef = useRef<HTMLDivElement>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  // Mock available actions for the sidebar
  const availableActions = [
    { id: "1", name: "Git Pull [Samples]", description: "Pull latest samples" },
    { id: "2", name: "Git Pull [Sweeps]", description: "Pull sweeps branch" },
    { id: "3", name: "Restart Apache", description: "Restart web server" },
    { id: "4", name: "Clear Cache", description: "Clear application cache" },
  ]

  useEffect(() => {
    if (sessionId) {
      setIsConnected(true)
      addLine("output", `Connected to session ${sessionId}`)
      addLine("output", "demo@test:/$")
    }
  }, [sessionId])

  useEffect(() => {
    // Auto-scroll to bottom when new lines are added
    if (scrollAreaRef.current) {
      const scrollContainer = scrollAreaRef.current.querySelector("[data-radix-scroll-area-viewport]")
      if (scrollContainer) {
        scrollContainer.scrollTop = scrollContainer.scrollHeight
      }
    }
  }, [lines])

  const addLine = (type: TerminalLine["type"], content: string) => {
    const newLine: TerminalLine = {
      id: Date.now().toString() + Math.random(),
      type,
      content,
      timestamp: new Date(),
    }
    setLines((prev) => [...prev, newLine])
  }

  const handleCommandSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!currentCommand.trim() || !isConnected) return

    // Add the command to terminal
    addLine("command", `demo@test:/$ ${currentCommand}`)

    // Simulate command execution
    setTimeout(() => {
      if (currentCommand.toLowerCase() === "help") {
        addLine("output", "Available commands: ls, pwd, whoami, date, clear, exit")
      } else if (currentCommand.toLowerCase() === "clear") {
        setLines([])
      } else if (currentCommand.toLowerCase() === "exit") {
        setIsConnected(false)
        addLine("output", "Connection closed.")
        return
      } else if (currentCommand.toLowerCase().startsWith("ls")) {
        addLine("output", "index.html  style.css  script.js  README.md")
      } else if (currentCommand.toLowerCase() === "pwd") {
        addLine("output", "/home/demo")
      } else if (currentCommand.toLowerCase() === "whoami") {
        addLine("output", "demo")
      } else if (currentCommand.toLowerCase() === "date") {
        addLine("output", new Date().toString())
      } else {
        addLine("error", `Command not found: ${currentCommand}`)
      }
      addLine("output", "demo@test:/$")
    }, 500)

    setCurrentCommand("")
  }

  const handleActionRun = (actionId: string) => {
    const action = availableActions.find((a) => a.id === actionId)
    if (!action || !isConnected) return

    addLine("command", `# Running action: ${action.name}`)

    // Simulate action execution
    setTimeout(() => {
      if (action.name.includes("Git Pull")) {
        addLine("output", "Fetching from origin...")
        setTimeout(() => {
          addLine("output", "Already up to date.")
          addLine("output", "demo@test:/$")
        }, 1000)
      } else if (action.name.includes("Restart")) {
        addLine("output", "Restarting Apache...")
        setTimeout(() => {
          addLine("output", "Apache restarted successfully.")
          addLine("output", "demo@test:/$")
        }, 1500)
      } else {
        addLine("output", `${action.name} completed successfully.`)
        addLine("output", "demo@test:/$")
      }
    }, 500)
  }

  if (!sessionId) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center">
          <Terminal className="w-16 h-16 text-muted-foreground mx-auto mb-4" />
          <h3 className="text-xl font-semibold mb-2">No Active Session</h3>
          <p className="text-muted-foreground">Connect to a session from the Sessions tab to access the terminal</p>
        </div>
      </div>
    )
  }

  return (
    <div className="flex h-full">
      {/* Terminal */}
      <div className="flex-1 flex flex-col">
        {/* Terminal Header */}
        <div className="bg-card border-b border-border p-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="flex gap-2">
                <div className="w-3 h-3 bg-red-500 rounded-full"></div>
                <div className="w-3 h-3 bg-yellow-500 rounded-full"></div>
                <div className="w-3 h-3 bg-green-500 rounded-full"></div>
              </div>
              <div className="flex items-center gap-2">
                <Terminal className="w-4 h-4" />
                <span className="font-medium">Terminal - Session {sessionId}</span>
                <Badge variant={isConnected ? "default" : "secondary"} className="text-xs">
                  {isConnected ? "Connected" : "Disconnected"}
                </Badge>
              </div>
            </div>
            <div className="flex items-center gap-2">
              <Button size="sm" variant="ghost">
                <Minimize2 className="w-4 h-4" />
              </Button>
              <Button size="sm" variant="ghost">
                <Maximize2 className="w-4 h-4" />
              </Button>
              <Button size="sm" variant="ghost">
                <Settings className="w-4 h-4" />
              </Button>
            </div>
          </div>
        </div>

        {/* Terminal Content */}
        <div className="flex-1 bg-black text-green-400 font-mono text-sm">
          <ScrollArea className="h-full" ref={scrollAreaRef}>
            <div className="p-4 space-y-1">
              {lines.map((line) => (
                <div
                  key={line.id}
                  className={`${
                    line.type === "command" ? "text-white" : line.type === "error" ? "text-red-400" : "text-green-400"
                  }`}
                >
                  {line.content}
                </div>
              ))}
            </div>
          </ScrollArea>

          {/* Command Input */}
          {isConnected && (
            <div className="border-t border-gray-700 p-4">
              <form onSubmit={handleCommandSubmit} className="flex items-center gap-2">
                <span className="text-green-400">demo@test:/$</span>
                <Input
                  ref={inputRef}
                  value={currentCommand}
                  onChange={(e) => setCurrentCommand(e.target.value)}
                  className="flex-1 bg-transparent border-none text-green-400 font-mono focus:ring-0 focus:outline-none"
                  placeholder="Enter command..."
                  autoFocus
                />
              </form>
            </div>
          )}
        </div>
      </div>

      {/* Actions Sidebar */}
      <div className="w-80 bg-card border-l border-border">
        <div className="p-4 border-b border-border">
          <h3 className="font-semibold flex items-center gap-2">
            <Play className="w-4 h-4" />
            Quick Actions
          </h3>
          <p className="text-sm text-muted-foreground">Run predefined commands</p>
        </div>

        <div className="p-4 space-y-3">
          {availableActions.map((action) => (
            <Card key={action.id} className="hover:shadow-sm transition-shadow cursor-pointer">
              <CardContent className="p-3">
                <div className="flex items-center justify-between">
                  <div>
                    <h4 className="font-medium text-sm">{action.name}</h4>
                    <p className="text-xs text-muted-foreground">{action.description}</p>
                  </div>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => handleActionRun(action.id)}
                    disabled={!isConnected}
                  >
                    <Play className="w-3 h-3" />
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        <div className="p-4 border-t border-border">
          <Button
            variant="outline"
            size="sm"
            className="w-full bg-transparent"
            onClick={() => inputRef.current?.focus()}
            disabled={!isConnected}
          >
            Focus Terminal
          </Button>
        </div>
      </div>
    </div>
  )
}
