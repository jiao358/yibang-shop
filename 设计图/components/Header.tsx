import { Search, MoreHorizontal, Target } from 'lucide-react'

export function Header() {
  return (
    <div className="bg-white">
      {/* Status Bar */}
      <div className="flex justify-between items-center px-4 py-1 text-sm text-gray-600">
        <span>17:28</span>
        <div className="flex items-center gap-1">
          <span className="text-xs">100</span>
          <div className="w-4 h-2 border border-gray-400 rounded-sm">
            <div className="w-full h-full bg-green-500 rounded-sm"></div>
          </div>
        </div>
      </div>
      
      {/* Search Header */}
      <div className="flex items-center justify-between px-4 py-3">
        <div className="flex items-center gap-3 flex-1">
          <h1 className="text-black">聚美箱包</h1>
          <div className="flex items-center gap-2 bg-gray-100 rounded-full px-3 py-1 flex-1 max-w-32">
            <Search className="w-4 h-4 text-gray-500" />
            <span className="text-sm text-gray-500">收纳</span>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <MoreHorizontal className="w-5 h-5 text-gray-600" />
          <Target className="w-5 h-5 text-gray-600" />
        </div>
      </div>
    </div>
  )
}