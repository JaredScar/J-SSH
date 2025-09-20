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
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Plus, Search, Zap, Edit, Trash2, Play, ArrowRight, Clock, ChevronDown, ChevronUp } from "lucide-react"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"

interface TriggerAction {
  actionId: string
  actionName: string
  order: number
}

interface Trigger {
  id: string
  name: string
  description: string
  server: string
  actions: TriggerAction[]
  lastRun?: string
  runCount: number
}

export function TriggersTab() {
  const [triggers, setTriggers] = useState<Trigger[]>([
    {
      id: "1",
      name: "Basic Trigger",
      description: "This is a basic trigger to give an example of their configuration...",
      server: "Jared Test Server",
      actions: [
        { actionId: "1", actionName: "Git Pull [Samples]", order: 1 },
        { actionId: "2", actionName: "Git Pull [Sweeps]", order: 2 },
      ],
      lastRun: "2 hours ago",
      runCount: 5,
    },
  ])

  const [searchQuery, setSearchQuery] = useState("")
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false)
  const [expandedTriggers, setExpandedTriggers] = useState<Set<string>>(new Set())
  const [newTrigger, setNewTrigger] = useState({
    name: "",
    description: "",
    server: "",
    selectedActions: [] as string[],
  })

  // Mock data for servers and actions
  const servers = ["Jared Test Server", "Rebex Test SSH"]
  const availableActions = [
    { id: "1", name: "Git Pull [Samples]" },
    { id: "2", name: "Git Pull [Sweeps]" },
    { id: "3", name: "Restart Apache" },
    { id: "4", name: "Clear Cache" },
  ]

  const filteredTriggers = triggers.filter(
    (trigger) =>
      trigger.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      trigger.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      trigger.server.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  const toggleTriggerExpansion = (triggerId: string) => {
    setExpandedTriggers((prev) => {
      const newSet = new Set(prev)
      if (newSet.has(triggerId)) {
        newSet.delete(triggerId)
      } else {
        newSet.add(triggerId)
      }
      return newSet
    })
  }

  const handleAddTrigger = () => {
    const trigger: Trigger = {
      id: Date.now().toString(),
      name: newTrigger.name,
      description: newTrigger.description,
      server: newTrigger.server,
      actions: newTrigger.selectedActions.map((actionId, index) => ({
        actionId,
        actionName: availableActions.find((a) => a.id === actionId)?.name || "",
        order: index + 1,
      })),
      runCount: 0,
    }

    setTriggers((prev) => [...prev, trigger])
    setNewTrigger({ name: "", description: "", server: "", selectedActions: [] })
    setIsAddDialogOpen(false)
  }

  const handleRunTrigger = (triggerId: string) => {
    setTriggers((prev) =>
      prev.map((trigger) =>
        trigger.id === triggerId ? { ...trigger, runCount: trigger.runCount + 1, lastRun: "Just now" } : trigger,
      ),
    )
    // Here you would integrate with the terminal to run the trigger sequence
    console.log("Running trigger:", triggerId)
  }

  return (
    <div className="flex flex-col h-full">
      {/* Header */}
      <div className="p-6 border-b border-border">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="text-2xl font-semibold text-balance">Triggers</h2>
            <p className="text-muted-foreground">Automated action sequences</p>
          </div>
          <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
            <DialogTrigger asChild>
              <Button className="gap-2">
                <Plus className="w-4 h-4" />
                Add Trigger
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-lg">
              <DialogHeader>
                <DialogTitle>Add New Trigger</DialogTitle>
                <DialogDescription>Create an automated sequence of actions</DialogDescription>
              </DialogHeader>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="triggerName">Name</Label>
                  <Input
                    id="triggerName"
                    value={newTrigger.name}
                    onChange={(e) => setNewTrigger((prev) => ({ ...prev, name: e.target.value }))}
                    placeholder="Deploy and Restart"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="triggerDescription">Description</Label>
                  <Textarea
                    id="triggerDescription"
                    value={newTrigger.description}
                    onChange={(e) => setNewTrigger((prev) => ({ ...prev, description: e.target.value }))}
                    placeholder="Describe what this trigger does..."
                    rows={3}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="server">Server</Label>
                  <Select
                    value={newTrigger.server}
                    onValueChange={(value) => setNewTrigger((prev) => ({ ...prev, server: value }))}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Select a server" />
                    </SelectTrigger>
                    <SelectContent>
                      {servers.map((server) => (
                        <SelectItem key={server} value={server}>
                          {server}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div className="space-y-2">
                  <Label>Actions (in order)</Label>
                  <div className="space-y-2 max-h-40 overflow-y-auto">
                    {availableActions.map((action) => (
                      <div key={action.id} className="flex items-center space-x-2">
                        <input
                          type="checkbox"
                          id={`action-${action.id}`}
                          checked={newTrigger.selectedActions.includes(action.id)}
                          onChange={(e) => {
                            if (e.target.checked) {
                              setNewTrigger((prev) => ({
                                ...prev,
                                selectedActions: [...prev.selectedActions, action.id],
                              }))
                            } else {
                              setNewTrigger((prev) => ({
                                ...prev,
                                selectedActions: prev.selectedActions.filter((id) => id !== action.id),
                              }))
                            }
                          }}
                          className="rounded border-border"
                        />
                        <Label htmlFor={`action-${action.id}`} className="text-sm">
                          {action.name}
                        </Label>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setIsAddDialogOpen(false)}>
                  Cancel
                </Button>
                <Button
                  onClick={handleAddTrigger}
                  disabled={!newTrigger.name || !newTrigger.server || newTrigger.selectedActions.length === 0}
                >
                  Add Trigger
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search triggers..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      {/* Triggers List */}
      <div className="flex-1 p-6 overflow-auto">
        <div className="space-y-4">
          {filteredTriggers.map((trigger) => (
            <Card key={trigger.id} className="hover:shadow-md transition-shadow">
              <Collapsible>
                <CardHeader className="pb-3">
                  <div className="flex items-start justify-between">
                    <div className="flex items-center gap-3 flex-1">
                      <div className="w-10 h-10 bg-accent/10 rounded-lg flex items-center justify-center">
                        <Zap className="w-5 h-5 text-accent" />
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center gap-2">
                          <CardTitle className="text-base">{trigger.name}</CardTitle>
                          <Badge variant="outline" className="text-xs">
                            {trigger.actions.length} actions
                          </Badge>
                        </div>
                        <CardDescription className="text-sm line-clamp-1">{trigger.description}</CardDescription>
                        <div className="flex items-center gap-4 mt-1 text-xs text-muted-foreground">
                          <span>Server: {trigger.server}</span>
                          {trigger.lastRun && (
                            <div className="flex items-center gap-1">
                              <Clock className="w-3 h-3" />
                              Last run: {trigger.lastRun}
                            </div>
                          )}
                          <span>Runs: {trigger.runCount}</span>
                        </div>
                      </div>
                    </div>
                    <CollapsibleTrigger asChild>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="h-8 w-8 p-0"
                        onClick={() => toggleTriggerExpansion(trigger.id)}
                      >
                        {expandedTriggers.has(trigger.id) ? (
                          <ChevronUp className="w-4 h-4" />
                        ) : (
                          <ChevronDown className="w-4 h-4" />
                        )}
                      </Button>
                    </CollapsibleTrigger>
                  </div>
                </CardHeader>
                <CollapsibleContent>
                  <CardContent className="space-y-4">
                    {/* Action Sequence */}
                    <div className="bg-muted/30 rounded-lg p-4">
                      <h4 className="text-sm font-medium mb-3">Action Sequence:</h4>
                      <div className="space-y-2">
                        {trigger.actions.map((action, index) => (
                          <div key={action.actionId} className="flex items-center gap-3">
                            <div className="w-6 h-6 bg-accent rounded-full flex items-center justify-center text-xs text-accent-foreground font-medium">
                              {action.order}
                            </div>
                            <span className="text-sm">{action.actionName}</span>
                            {index < trigger.actions.length - 1 && (
                              <ArrowRight className="w-4 h-4 text-muted-foreground ml-auto" />
                            )}
                          </div>
                        ))}
                      </div>
                    </div>

                    {/* Actions */}
                    <div className="flex gap-2">
                      <Button size="sm" className="flex-1 gap-2" onClick={() => handleRunTrigger(trigger.id)}>
                        <Play className="w-3 h-3" />
                        Run Trigger
                      </Button>
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
                </CollapsibleContent>
              </Collapsible>
            </Card>
          ))}
        </div>

        {filteredTriggers.length === 0 && (
          <div className="text-center py-12">
            <Zap className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
            <h3 className="text-lg font-medium mb-2">No triggers found</h3>
            <p className="text-muted-foreground mb-4">
              {searchQuery ? "Try adjusting your search terms" : "Create your first automated trigger sequence"}
            </p>
            {!searchQuery && (
              <Button onClick={() => setIsAddDialogOpen(true)} className="gap-2">
                <Plus className="w-4 h-4" />
                Add Trigger
              </Button>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
