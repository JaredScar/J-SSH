"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Plus, Search, Server, Edit, Trash2, Play, Clock, Wifi, WifiOff } from "lucide-react"

interface Session {
  id: string
  nickname: string
  host: string
  username: string
  status: "connected" | "disconnected" | "connecting"
  lastUsed: string
  iconUrl?: string
}

interface SessionsTabProps {
  onSessionConnect: (sessionId: string) => void
}

export function SessionsTab({ onSessionConnect }: SessionsTabProps) {
  const [sessions, setSessions] = useState<Session[]>([
    {
      id: "1",
      nickname: "Jared Test Server",
      host: "147.189.170.243",
      username: "root",
      status: "disconnected",
      lastUsed: "2 hours ago",
    },
    {
      id: "2",
      nickname: "Rebex Test SSH",
      host: "test.rebex.net",
      username: "demo",
      status: "connected",
      lastUsed: "5 minutes ago",
    },
  ])

  const [searchQuery, setSearchQuery] = useState("")
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false)
  const [newSession, setNewSession] = useState({
    nickname: "",
    host: "",
    username: "",
    password: "",
    privateKeyPath: "",
    iconUrl: "",
  })

  const filteredSessions = sessions.filter(
    (session) =>
      session.nickname.toLowerCase().includes(searchQuery.toLowerCase()) ||
      session.host.toLowerCase().includes(searchQuery.toLowerCase()) ||
      session.username.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  const handleConnect = (sessionId: string) => {
    setSessions((prev) =>
      prev.map((session) => (session.id === sessionId ? { ...session, status: "connecting" as const } : session)),
    )

    // Simulate connection
    setTimeout(() => {
      setSessions((prev) =>
        prev.map((session) =>
          session.id === sessionId ? { ...session, status: "connected" as const, lastUsed: "Just now" } : session,
        ),
      )
      onSessionConnect(sessionId)
    }, 1500)
  }

  const handleDisconnect = (sessionId: string) => {
    setSessions((prev) =>
      prev.map((session) => (session.id === sessionId ? { ...session, status: "disconnected" as const } : session)),
    )
  }

  const handleAddSession = () => {
    const session: Session = {
      id: Date.now().toString(),
      nickname: newSession.nickname,
      host: newSession.host,
      username: newSession.username,
      status: "disconnected",
      lastUsed: "Never",
    }

    setSessions((prev) => [...prev, session])
    setNewSession({
      nickname: "",
      host: "",
      username: "",
      password: "",
      privateKeyPath: "",
      iconUrl: "",
    })
    setIsAddDialogOpen(false)
  }

  const getStatusIcon = (status: Session["status"]) => {
    switch (status) {
      case "connected":
        return <Wifi className="w-4 h-4 text-green-500" />
      case "connecting":
        return <div className="w-4 h-4 border-2 border-accent border-t-transparent rounded-full animate-spin" />
      case "disconnected":
        return <WifiOff className="w-4 h-4 text-muted-foreground" />
    }
  }

  const getStatusBadge = (status: Session["status"]) => {
    switch (status) {
      case "connected":
        return (
          <Badge variant="default" className="bg-green-500 hover:bg-green-600">
            Connected
          </Badge>
        )
      case "connecting":
        return <Badge variant="secondary">Connecting...</Badge>
      case "disconnected":
        return <Badge variant="outline">Disconnected</Badge>
    }
  }

  return (
    <div className="flex flex-col h-full">
      {/* Header */}
      <div className="p-6 border-b border-border">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="text-2xl font-semibold text-balance">SSH Sessions</h2>
            <p className="text-muted-foreground">Manage your server connections</p>
          </div>
          <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
            <DialogTrigger asChild>
              <Button className="gap-2">
                <Plus className="w-4 h-4" />
                Add Session
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-md">
              <DialogHeader>
                <DialogTitle>Add New Session</DialogTitle>
                <DialogDescription>Create a new SSH connection configuration</DialogDescription>
              </DialogHeader>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="nickname">Nickname</Label>
                  <Input
                    id="nickname"
                    value={newSession.nickname}
                    onChange={(e) => setNewSession((prev) => ({ ...prev, nickname: e.target.value }))}
                    placeholder="My Server"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="host">Host/IP Address</Label>
                  <Input
                    id="host"
                    value={newSession.host}
                    onChange={(e) => setNewSession((prev) => ({ ...prev, host: e.target.value }))}
                    placeholder="192.168.1.100"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="username">Username</Label>
                  <Input
                    id="username"
                    value={newSession.username}
                    onChange={(e) => setNewSession((prev) => ({ ...prev, username: e.target.value }))}
                    placeholder="root"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="password">Password</Label>
                  <Input
                    id="password"
                    type="password"
                    value={newSession.password}
                    onChange={(e) => setNewSession((prev) => ({ ...prev, password: e.target.value }))}
                    placeholder="••••••••"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="privateKey">Private Key Path (Optional)</Label>
                  <Input
                    id="privateKey"
                    value={newSession.privateKeyPath}
                    onChange={(e) => setNewSession((prev) => ({ ...prev, privateKeyPath: e.target.value }))}
                    placeholder="/path/to/private/key"
                  />
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setIsAddDialogOpen(false)}>
                  Cancel
                </Button>
                <Button
                  onClick={handleAddSession}
                  disabled={!newSession.nickname || !newSession.host || !newSession.username}
                >
                  Add Session
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search sessions..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      {/* Sessions Grid */}
      <div className="flex-1 p-6 overflow-auto">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredSessions.map((session) => (
            <Card key={session.id} className="hover:shadow-md transition-shadow">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-card rounded-lg flex items-center justify-center border">
                      <Server className="w-5 h-5 text-muted-foreground" />
                    </div>
                    <div>
                      <CardTitle className="text-base">{session.nickname}</CardTitle>
                      <CardDescription className="text-sm">
                        {session.username}@{session.host}
                      </CardDescription>
                    </div>
                  </div>
                  {getStatusIcon(session.status)}
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center justify-between">
                  {getStatusBadge(session.status)}
                  <div className="flex items-center gap-1 text-xs text-muted-foreground">
                    <Clock className="w-3 h-3" />
                    {session.lastUsed}
                  </div>
                </div>

                <div className="flex gap-2">
                  {session.status === "connected" ? (
                    <Button
                      size="sm"
                      variant="outline"
                      className="flex-1 bg-transparent"
                      onClick={() => handleDisconnect(session.id)}
                    >
                      Disconnect
                    </Button>
                  ) : (
                    <Button
                      size="sm"
                      className="flex-1 gap-2"
                      onClick={() => handleConnect(session.id)}
                      disabled={session.status === "connecting"}
                    >
                      <Play className="w-3 h-3" />
                      Connect
                    </Button>
                  )}
                  <Button size="sm" variant="outline">
                    <Edit className="w-3 h-3" />
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    className="text-destructive hover:text-destructive bg-transparent"
                  >
                    <Trash2 className="w-3 h-3" />
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {filteredSessions.length === 0 && (
          <div className="text-center py-12">
            <Server className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
            <h3 className="text-lg font-medium mb-2">No sessions found</h3>
            <p className="text-muted-foreground mb-4">
              {searchQuery ? "Try adjusting your search terms" : "Get started by adding your first SSH session"}
            </p>
            {!searchQuery && (
              <Button onClick={() => setIsAddDialogOpen(true)} className="gap-2">
                <Plus className="w-4 h-4" />
                Add Session
              </Button>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
