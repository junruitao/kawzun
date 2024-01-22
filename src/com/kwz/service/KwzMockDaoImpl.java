package com.kwz.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.kwz.entity.Catalog;
import com.kwz.entity.News;
import com.kwz.entity.Product;
import com.kwz.entity.User;
import com.kwz.enums.EntityType;
import com.kwz.enums.NewsType;
import com.kwz.enums.UserRole;

//@Service("kwzDao")
public class KwzMockDaoImpl implements KwzDao {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(KwzMockDaoImpl.class.getName());
    final String imageFolder = "images/";
    final String[] imgs = { "s001.jpg", "s002.jpg", "s003.jpg", "s005.jpg", "s006.jpg", "s007.jpg", "s008.jpg", "s011.jpg", "s013.jpg", "s014.jpg" };
    List<Catalog> catalogs;
    List<News> news;
    List<User> users;
    private static final String mockCats[] = { "乐驰连体", "新赛欧三厢" };
    private static final String mockNames[] = { "米黑", "红黑", "蓝黑" };
    private static final String mockNews[] = { "卡唯尊汽车座垫用户满意度最高", "卡唯尊汽车座垫获年度推荐产品大奖", "卡唯尊邀您为汽车下乡 “谏言献策”", "新年伊始，媒体关注卡唯尊创新", "卡唯尊汽车座垫首获中国低碳认证001号证书" };
    private static final String newsContent = "温岭市卡唯尊汽车座垫厂是一家从事汽车用品生产和销售的企业。公司成立以来,以其规范的运作模式、完善的销售机制、高质量的服务体系，在广大客户中赢得盛誉。现已在全国各地建立庞大稳定的销售网络。并呈现不断扩展的趋势。"
            + "“专业是基础，人才是根本”。卡唯尊拥有专业的设计团队、专业的技术技师、现代化的工厂设备，确保每一款产品创意新颖、品质优异，致力于塑造品牌形象，打造企业诚信。" + "卡唯尊秉着“精益求精，追求卓越”的宗旨和“以人为本，客户至上”的理念，让您见证每一款产品的成功。"
            + "今天卡唯尊员工奉行“进取 求实 严谨 团结”的方针，不断开拓创新，以技术为核心、视质量为生命、奉用户为上帝，竭诚为您提供性价比最高的自控产品、高质量的工程设计改造及无微不至的售后服务。";

    public List<Catalog> getCatalogs() {
        if (catalogs == null) {
            catalogs = new ArrayList<Catalog>();
            for (String mcat : mockCats) {
                Catalog cat = new Catalog();
                cat.setName(mcat);
                catalogs.add(cat);
                for (String name : mockNames) {
                    Product p = new Product();
                    // p.setId("pid" + i + j);
                    p.setName(mcat + name);
                    p.setImageLink(getRandomImage());
                    p.setPopular(getRandomBoolean());
                    p.setDiscount(getRandomBoolean());
                    p.setShortDescription("品牌: 菲都狄都内饰材质: 三明治面料颜色分类: 新赛欧三厢蓝黑色... 内饰图案: 汽车品牌LOGO 适合车型: 新乐风 雪佛兰乐风… ");
                    p.setLongDescription("我们在展会的主场建造和特型展台的设计、搭建方面具有国际先进水准和国内顶级水平，同时提供拥有多年从业经验的会展行销顾问和执行团队，为您提供全程策划和执行。"
                            + "我们以专业策划群体和高水准制作为基础，建立了一套完善的服务理验。从策划、设计、制作到实地协调，组织安排，都有严格细致的分工工作。在与众多客户深层次的探讨与交流中，积累了大量的执行和操作经验。加上勤奋务实的工作作风，先进的设备和雄厚的技术力量，为广大客户展示了企业综合实力，树立企业形象，提供最有效的服务。");
                    cat.addProduct(p);
                }
            }
        }
        return catalogs;
    }

    private String getRandomImage() {
        return imageFolder + imgs[new Random().nextInt(imgs.length)];
    }

    private boolean getRandomBoolean() {
        return new Random().nextBoolean();
    }

    public Catalog newCatalog() {
        Catalog cat = new Catalog();
        // cat.setId("catID" + UUID.randomUUID().toString());
        cat.setName(cat.getId());
        return cat;
    }

    public Product newProduct() {
        Product p = new Product();
        // p.setId("prdID" + UUID.randomUUID().toString());
        p.setName(p.getId());
        p.setImageLink(getRandomImage());
        return p;
    }

    @Override
    public List<News> getNews() {
        if (news == null) {
            news = new ArrayList<News>();
            for (String t : mockNews) {
                News aNews = new News();
                aNews.setNewsType(getRandomBoolean() ? NewsType.company : NewsType.industry);
                aNews.setName(t);
                aNews.setContent(newsContent);
                news.add(aNews);
            }
        }
        return news;
    }

    @Override
    public News newNews() {
        News news = new News();
        news.setNewsType(NewsType.company);
        // news.setId("newsID" + UUID.randomUUID().toString());
        news.setName("title_ new");
        news.setContent("contnent_new");
        return news;
    }

    @Override
    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<User>();
            User u = new User("admin", "admin@email", UserRole.ROLE_ADMIN.name(), "Password1");
            u.digestPassword();
            users.add(u);
            u = new User("userAdmin", "useradmin@email", UserRole.ROLE_USER_ADMIN.name() + "," + UserRole.ROLE_ADMIN.name(), "Password1");
            u.digestPassword();
            users.add(u);
        }
        return users;
    }

    // @Override
    // public User getUser(String name) {
    // User user = new User();
    // user.setName("admin");
    // user.setPassword("");
    // return user;
    // }

    @Override
    public void setChanged(EntityType type) {
    }

    @Override
    public User newUser() {
        User newUser = new User();
        return newUser;
    }

    // // update a single entity without changing the order
    // public <T extends IdedTimestampedBase> void update(T e) {
    // }
    //
    // @Override
    // public void addProduct(Catalog cat, Product p) {
    // cat.addProduct(p);
    // }
    //
    // @Override
    // public void addCatalog(Catalog c) {
    // catalogs.add(c);
    // }
    //
    // @Override
    // public void adjustAll() {
    // }
    //
    // @Override
    // public void removeProduct(Catalog catalog, Product o) {
    // catalog.getProducts().remove(o);
    // }
    //
    // @Override
    // public void removeCatalog(Catalog o) {
    // getCatalogs().remove(o);
    // }
    //
    // @Override
    // public void removeNews(News o) {
    // getNews().remove(o);
    // }
}
