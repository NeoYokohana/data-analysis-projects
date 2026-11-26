from django.views import View
from utils.goods import get_categories
from apps.contents.models import ContentCategory
from django.shortcuts import render
from django.core.paginator import Paginator, EmptyPage
from apps.goods.models import SKU, GoodsCategory
from django.http import JsonResponse
from utils.goods import get_breadcrumb

class IndexView(View):

    def get(self,request):

        """
        首页的数据分为2部分
        1部分是 商品分类数据
        2部分是 广告数据

        """
        # 1.商品分类数据
        categories=get_categories()
        # 2.广告数据
        contents = {}
        content_categories = ContentCategory.objects.all()
        for cat in content_categories:
            contents[cat.key] = cat.content_set.filter(status=True).order_by('sequence')

        # 我们的首页 后边会讲解页面静态化
        # 我们把数据 传递 给 模板
        context = {
            'categories': categories,
            'contents': contents,
        }
        # 模板使用比较少，以后大家到公司 自然就会了
        return render(request,'index.html',context)

class ListView(View):
    """商品列表页"""

    def get(self, request, category_id):
        """提供商品列表页"""
        # 获取参数:
        page = request.GET.get('page')
        page_size = request.GET.get('page_size')
        ordering = request.GET.get('ordering')

        # 判断category_id是否正确
        try:
            # 获取三级菜单分类信息:
            category = GoodsCategory.objects.get(id=category_id)
        except Exception as e:
            return JsonResponse({'code':400,
                                 'errmsg':'获取mysql数据出错'})

        # 查询面包屑导航(函数在下面写着)
        breadcrumb = get_breadcrumb(category)

        # 排序方式:
        try:
            skus = SKU.objects.filter(category=category,
                                      is_launched=True).order_by(ordering)
        except Exception as e:
            return JsonResponse({'code':400,
                                 'errmsg':'获取mysql数据出错'})

        paginator = Paginator(skus, page_size)
        # 获取每页商品数据
        try:
            page_skus = paginator.page(page)
        except EmptyPage:
            # 如果page_num不正确，默认给用户400
            return JsonResponse({'code':400,
                                 'errmsg':'page数据出错'})
        # 获取列表页总页数
        total_page = paginator.num_pages

        # 定义列表:
        list = []
        # 整理格式:
        for sku in page_skus:
            list.append({
                'id':sku.id,
                'default_image_url':sku.default_image.url,
                'name':sku.name,
                'price':sku.price
            })

        # 把数据变为 json 发送给前端
        return JsonResponse({
                             'code':0,
                             'errmsg':'ok',
                             'breadcrumb': breadcrumb,
                             'list':list,
                             'count':total_page
                            })

class HotGoodsView(View):
    """商品热销排行"""

    def get(self, request, category_id):
        """提供商品热销排行JSON数据"""
        # 根据销量倒序
        skus = SKU.objects.filter(category_id=category_id, is_launched=True).order_by('-sales')[:2]

        # 序列化
        hot_skus = []
        for sku in skus:
            hot_skus.append({
                'id':sku.id,
                'default_image_url':sku.default_image.url,
                'name':sku.name,
                'price':sku.price
            })

        return JsonResponse({'code':0, 'errmsg':'OK', 'hot_skus':hot_skus})

from utils.goods import get_categories
from utils.goods import get_breadcrumb
from utils.goods import get_goods_specs
class DetailView(View):

    def get(self,request,sku_id):
        try:
            sku=SKU.objects.get(id=sku_id)
        except SKU.DoesNotExist:
            pass
        # 1.分类数据
        categories=get_categories()
        # 2.面包屑
        breadcrumb=get_breadcrumb(sku.category)
        # 3.SKU信息
        # 4.规格信息
        goods_specs=get_goods_specs(sku)

        context = {

            'categories': categories,
            'breadcrumb': breadcrumb,
            'sku': sku,
            'specs': goods_specs,

        }
        return render(request,'detail.html',context)

import os
import time
from django.conf import settings
from django.template import loader
from apps.contents.models import ContentCategory
from utils.goods import get_categories

def generate_static_index_html():
    """
    生成静态的主页html文件
    """
    print('%s: generate_static_index_html' % time.ctime())

    # 获取商品频道和分类
    categories = get_categories()

    # 广告内容
    contents = {}
    content_categories = ContentCategory.objects.all()
    for cat in content_categories:
        contents[cat.key] = cat.content_set.filter(status=True).order_by('sequence')

    # 渲染模板
    context = {
        'categories': categories,
        'contents': contents
    }

    # 获取首页模板文件
    template = loader.get_template('index.html')
    # 渲染首页html字符串
    html_text = template.render(context)
    # 将首页html字符串写入到指定目录，命名'index.html'
    file_path = os.path.join(os.path.dirname(settings.BASE_DIR), 'front_end_pc/index.html')
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(html_text)

# Create your views here.
