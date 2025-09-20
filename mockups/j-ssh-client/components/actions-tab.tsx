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
import { Plus, Search, Play, Edit, Trash2, Terminal, Copy } from "lucide-react"

interface Action {
  id: string
  name: string
  commands: string[]
  description?: string
  usageCount: number
}

export function ActionsTab() {
  const [actions, setActions] = useState<Action[]>([
    {
      id: "1",
      name: "Git Pull [Samples]",
      commands: ["cd /var/www/html", "git pull origin main"],
      description: "Pull latest changes from samples repository",
      usageCount: 15,
    },
    {
      id: "2",
      name: "Git Pull [Sweeps]",
      commands: ["cd /var/www/html", "git pull origin sweeps"],
      description: "Pull latest changes from sweeps branch",
      usageCount: 8,
    },
  ])

  const [searchQuery, setSearchQuery] = useState("")
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false)
  const [newAction, setNewAction] = useState({
    name: "",
    commands: "",
    description: "",
  })

  const filteredActions = actions.filter(
    (action) =>
      action.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      action.commands.some((cmd) => cmd.toLowerCase().includes(searchQuery.toLowerCase())),
  )

  const handleAddAction = () => {
    const action: Action = {
      id: Date.now().toString(),
      name: newAction.name,
      commands: newAction.commands.split("\n").filter((cmd) => cmd.trim()),
      description: newAction.description,
      usageCount: 0,
    }

    setActions((prev) => [...prev, action])
    setNewAction({ name: "", commands: "", description: "" })
    setIsAddDialogOpen(false)
  }

  const handleRunAction = (actionId: string) => {
    setActions((prev) =>
      prev.map((action) => (action.id === actionId ? { ...action, usageCount: action.usageCount + 1 } : action)),
    )
    // Here you would integrate with the terminal to run the commands
    console.log("Running action:", actionId)
  }

  return (
    <div className="flex flex-col h-full">
      {/* Header */}
      <div className="p-6 border-b border-border">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="text-2xl font-semibold text-balance">Actions</h2>
            <p className="text-muted-foreground">Reusable command sequences</p>
          </div>
          <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
            <DialogTrigger asChild>
              <Button className="gap-2">
                <Plus className="w-4 h-4" />
                Add Action
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-md">
              <DialogHeader>
                <DialogTitle>Add New Action</DialogTitle>
                <DialogDescription>Create a reusable command sequence</DialogDescription>
              </DialogHeader>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="actionName">Action Name</Label>
                  <Input
                    id="actionName"
                    value={newAction.name}
                    onChange={(e) => setNewAction((prev) => ({ ...prev, name: e.target.value }))}
                    placeholder="Deploy Application"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="commands">Commands (one per line)</Label>
                  <Textarea
                    id="commands"
                    value={newAction.commands}
                    onChange={(e) => setNewAction((prev) => ({ ...prev, commands: e.target.value }))}
                    placeholder="cd /var/www/html&#10;git pull origin main&#10;npm install&#10;npm run build"
                    rows={6}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="description">Description (Optional)</Label>
                  <Input
                    id="description"
                    value={newAction.description}
                    onChange={(e) => setNewAction((prev) => ({ ...prev, description: e.target.value }))}
                    placeholder="Brief description of what this action does"
                  />
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setIsAddDialogOpen(false)}>
                  Cancel
                </Button>
                <Button onClick={handleAddAction} disabled={!newAction.name || !newAction.commands}>
                  Add Action
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search actions..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      {/* Actions List */}
      <div className="flex-1 p-6 overflow-auto">
        <div className="space-y-4">
          {filteredActions.map((action) => (
            <Card key={action.id} className="hover:shadow-md transition-shadow">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-accent/10 rounded-lg flex items-center justify-center">
                      <Terminal className="w-5 h-5 text-accent" />
                    </div>
                    <div>
                      <CardTitle className="text-base">{action.name}</CardTitle>
                      {action.description && (
                        <CardDescription className="text-sm">{action.description}</CardDescription>
                      )}
                    </div>
                  </div>
                  <Badge variant="secondary" className="text-xs">
                    Used {action.usageCount} times
                  </Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="bg-muted/30 rounded-lg p-3">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-medium text-muted-foreground">Commands:</span>
                    <Button size="sm" variant="ghost" className="h-6 w-6 p-0">
                      <Copy className="w-3 h-3" />
                    </Button>
                  </div>
                  <div className="space-y-1">
                    {action.commands.map((command, index) => (
                      <div key={index} className="text-sm font-mono text-foreground">
                        <span className="text-muted-foreground mr-2">$</span>
                        {command}
                      </div>
                    ))}
                  </div>
                </div>

                <div className="flex gap-2">
                  <Button size="sm" className="flex-1 gap-2" onClick={() => handleRunAction(action.id)}>
                    <Play className="w-3 h-3" />
                    Run Action
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
            </Card>
          ))}
        </div>

        {filteredActions.length === 0 && (
          <div className="text-center py-12">
            <Terminal className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
            <h3 className="text-lg font-medium mb-2">No actions found</h3>
            <p className="text-muted-foreground mb-4">
              {searchQuery ? "Try adjusting your search terms" : "Create your first reusable command action"}
            </p>
            {!searchQuery && (
              <Button onClick={() => setIsAddDialogOpen(true)} className="gap-2">
                <Plus className="w-4 h-4" />
                Add Action
              </Button>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
