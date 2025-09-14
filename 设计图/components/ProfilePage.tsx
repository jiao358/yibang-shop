import { MoreHorizontal, Target, User, Eye, Map, Settings, Bell, BookOpen, Headphones } from 'lucide-react'
import exampleImage from 'figma:asset/9f7628a9d7c8c815119e0baf6f9ee30d4ea7f350.png'

export function ProfilePage() {
  return (
    <div className="bg-gray-50 min-h-screen">
      {/* Status Bar */}
      <div className="flex justify-between items-center px-4 py-1 text-sm text-gray-600 bg-white">
        <span>17:29</span>
        <div className="flex items-center gap-1">
          <span className="text-xs">100</span>
          <div className="w-4 h-2 border border-gray-400 rounded-sm">
            <div className="w-full h-full bg-green-500 rounded-sm"></div>
          </div>
        </div>
      </div>

      {/* Header */}
      <div className="flex items-center justify-end px-4 py-3 bg-white">
        <div className="flex items-center gap-3">
          <MoreHorizontal className="w-5 h-5 text-gray-600" />
          <Target className="w-5 h-5 text-gray-600" />
        </div>
      </div>

      {/* User Profile Section */}
      <div className="bg-white px-4 py-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 bg-pink-300 rounded-full flex items-center justify-center">
              <User className="w-8 h-8 text-white" />
            </div>
            <div>
              <h2 className="text-lg text-black">微信用户</h2>
              <p className="text-gray-500">ID：3585076</p>
              <p className="text-gray-400 text-sm">暂无简介</p>
            </div>
          </div>
          <Target className="w-6 h-6 text-gray-600" />
        </div>

        {/* Balance Section */}
        <div className="grid grid-cols-3 gap-6 text-center mb-6">
          <div>
            <div className="text-xl text-pink-400 mb-1">0.00</div>
            <div className="text-sm text-gray-500">余额</div>
          </div>
          <div>
            <div className="text-xl text-pink-400 mb-1">0.00</div>
            <div className="text-sm text-gray-500">完成 0 单</div>
          </div>
          <div>
            <div className="text-xl text-pink-400 mb-1">0.00</div>
            <div className="text-sm text-gray-500">邀请奖励</div>
          </div>
        </div>

        {/* Verification Section */}
        <div className="flex items-center justify-between p-3 bg-pink-50 rounded-lg mb-4">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 bg-pink-200 rounded flex items-center justify-center">
              <span className="text-pink-600 text-sm">认</span>
            </div>
            <span className="text-gray-700">请先完成模特认证</span>
          </div>
          <button className="bg-pink-400 text-white px-4 py-1 rounded-full text-sm">
            立即认证
          </button>
        </div>

        {/* Promotion Section */}
        <div className="bg-gradient-to-r from-pink-400 to-pink-500 rounded-lg p-4 text-white">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-lg mb-1">邀联通告</h3>
              <p className="text-sm opacity-90">海量商家等您/种草通告，邀请与您合作</p>
            </div>
            <button className="bg-white bg-opacity-20 px-4 py-2 rounded-full text-sm">
              立即加入
            </button>
          </div>
        </div>
      </div>

      {/* Menu Grid */}
      <div className="bg-white mx-4 mt-4 rounded-lg p-4">
        <div className="grid grid-cols-4 gap-6">
          <div className="flex flex-col items-center">
            <Eye className="w-6 h-6 text-gray-600 mb-2" />
            <span className="text-sm text-gray-600">网拍相册</span>
          </div>
          <div className="flex flex-col items-center">
            <BookOpen className="w-6 h-6 text-gray-600 mb-2" />
            <span className="text-sm text-gray-600">种草相册</span>
          </div>
          <div className="flex flex-col items-center">
            <Eye className="w-6 h-6 text-gray-600 mb-2" />
            <span className="text-sm text-gray-600">意见反馈</span>
          </div>
          <div className="flex flex-col items-center">
            <BookOpen className="w-6 h-6 text-gray-600 mb-2" />
            <span className="text-sm text-gray-600">浏览记录</span>
          </div>
          <div className="flex flex-col items-center">
            <Map className="w-6 h-6 text-gray-600 mb-2" />
            <span className="text-sm text-gray-600">收货地址</span>
          </div>
          <div className="flex flex-col items-center">
            <Bell className="w-6 h-6 text-gray-600 mb-2" />
            <span className="text-sm text-gray-600">通知设置</span>
          </div>
          <div className="flex flex-col items-center">
            <BookOpen className="w-6 h-6 text-gray-600 mb-2" />
            <span className="text-sm text-gray-600">新手必读</span>
          </div>
          <div className="flex flex-col items-center">
            <Headphones className="w-6 h-6 text-gray-600 mb-2" />
            <span className="text-sm text-gray-600">联系客服</span>
          </div>
        </div>
      </div>
    </div>
  )
}