import { Home, Eye, ShoppingBag, MessageCircle, User } from 'lucide-react'

interface BottomNavProps {
  activeTab: string
  onTabChange: (tab: string) => void
}

export function BottomNav({ activeTab, onTabChange }: BottomNavProps) {
  const navItems = [
    { id: 'home', icon: Home, label: '首页' },
    { id: 'browse', icon: Eye, label: '精选案例' },
    { id: 'orders', icon: ShoppingBag, label: '订单' },
    { id: 'messages', icon: MessageCircle, label: '消息' },
    { id: 'profile', icon: User, label: '我的' }
  ]

  return (
    <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-2">
      <div className="flex items-center justify-around">
        {navItems.map((item) => (
          <button
            key={item.id}
            onClick={() => onTabChange(item.id)}
            className={`flex flex-col items-center gap-1 py-1 ${
              activeTab === item.id ? 'text-red-500' : 'text-gray-600'
            }`}
          >
            <item.icon className="w-5 h-5" />
            <span className="text-xs">{item.label}</span>
          </button>
        ))}
      </div>
    </div>
  )
}