import { ArrowLeft, MoreHorizontal, Target } from 'lucide-react'
import exampleImage from 'figma:asset/1f74fa7918bbacfc41f2acb89fb12d3031ce9031.png'

export function BrowsingHistoryPage() {
  return (
    <div className="bg-white min-h-screen">
      {/* Status Bar */}
      <div className="flex justify-between items-center px-4 py-1 text-sm text-gray-600">
        <span>17:30</span>
        <div className="flex items-center gap-1">
          <span className="text-xs">100</span>
          <div className="w-4 h-2 border border-gray-400 rounded-sm">
            <div className="w-full h-full bg-green-500 rounded-sm"></div>
          </div>
        </div>
      </div>

      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
        <ArrowLeft className="w-6 h-6 text-gray-600" />
        <h1 className="text-lg text-black">浏览记录</h1>
        <div className="flex items-center gap-3">
          <MoreHorizontal className="w-5 h-5 text-gray-600" />
          <Target className="w-5 h-5 text-gray-600" />
        </div>
      </div>

      {/* Tab Navigation */}
      <div className="flex items-center justify-around px-4 py-3 border-b border-gray-100">
        <div className="flex flex-col items-center">
          <span className="text-gray-600">收藏</span>
          <span className="text-sm text-gray-400">0</span>
        </div>
        <div className="flex flex-col items-center">
          <span className="text-gray-600">关注</span>
          <span className="text-sm text-gray-400">0</span>
        </div>
        <div className="flex flex-col items-center">
          <span className="text-red-500 border-b-2 border-red-500 pb-1">足迹</span>
          <span className="text-sm text-gray-400">0</span>
        </div>
        <div className="flex flex-col items-center">
          <span className="text-gray-600">拉黑通告</span>
          <span className="text-sm text-gray-400">0</span>
        </div>
        <div className="flex flex-col items-center">
          <span className="text-red-500">编辑</span>
        </div>
      </div>

      {/* Empty State */}
      <div className="flex flex-col items-center justify-center flex-1 px-8 py-16">
        <img 
          src={exampleImage} 
          alt="暂无数据" 
          className="w-48 h-48 object-contain mb-4"
        />
        <p className="text-gray-400 text-center">暂无数据</p>
      </div>

      {/* Home indicator */}
      <div className="flex justify-center py-4">
        <div className="w-32 h-1 bg-gray-300 rounded-full"></div>
      </div>
    </div>
  )
}