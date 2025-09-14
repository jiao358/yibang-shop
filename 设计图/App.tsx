import { useState } from 'react'
import { HomePage } from './components/HomePage'
import { BrowsingHistoryPage } from './components/BrowsingHistoryPage'
import { InviteEarningsPage } from './components/InviteEarningsPage'
import { MessagePage } from './components/MessagePage'
import { ProfilePage } from './components/ProfilePage'
import { BottomNav } from './components/BottomNav'

export default function App() {
  const [activeTab, setActiveTab] = useState('home')

  const renderCurrentPage = () => {
    switch (activeTab) {
      case 'home':
        return <HomePage />
      case 'browse':
        return <BrowsingHistoryPage />
      case 'orders':
        return <InviteEarningsPage />
      case 'messages':
        return <MessagePage />
      case 'profile':
        return <ProfilePage />
      default:
        return <HomePage />
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 pb-20">
      {renderCurrentPage()}
      <BottomNav activeTab={activeTab} onTabChange={setActiveTab} />
    </div>
  )
}