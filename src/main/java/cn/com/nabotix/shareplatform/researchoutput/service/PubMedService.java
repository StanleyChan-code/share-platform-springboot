package cn.com.nabotix.shareplatform.researchoutput.service;

import cn.com.nabotix.shareplatform.researchoutput.entity.OutputType;
import cn.com.nabotix.shareplatform.researchoutput.entity.ResearchOutput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * PubMed服务类，用于从PubMed数据库获取科研论文数据并转换为ResearchOutput对象
 * <p>
 * 该服务通过PubMed E-utilities API获取论文的详细信息，包括标题、摘要、作者、
 * 期刊信息、引用次数等，并将其封装成统一的ResearchOutput格式。
 * <p>
 * 主要功能：
 * 1. 根据PubMed ID获取论文数据
 * 2. 解析XML格式的元数据
 * 3. 提取论文的基本信息、作者、期刊、摘要等
 * 4. 获取引用次数和其他附加信息
 *
 * @author 陈雍文
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PubMedService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 根据PubMed ID获取论文数据并转换为ResearchOutput对象
     *
     * @param pubMedId PubMed唯一标识符
     * @return ResearchOutput 科研成果对象
     * @throws IOException 当从PubMed获取数据失败时抛出
     */
    public ResearchOutput fetchPaperAsResearchOutput(String pubMedId) throws IOException {
        log.info("Fetching PubMed data for ID: {} and converting to ResearchOutput", pubMedId);

        // 构建PubMed E-utilities API请求URL (仅使用efetch)
        String detailUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id=" + pubMedId + "&retmode=xml";
        // 发送HTTP请求获取XML格式的数据
        String detailXml = restTemplate.getForObject(detailUrl, String.class);
        // 构建查询PMC链接的URL
        String pmcUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi?dbfrom=pubmed&db=pmc&id=" + pubMedId + "&retmode=json";
        String pmcJson = restTemplate.getForObject(pmcUrl, String.class);

        // 检查返回数据是否为空
        if (detailXml == null) {
            throw new IOException("Failed to fetch data from PubMed");
        }

        try {
            // 使用Jsoup解析XML
            Document document = Jsoup.parse(detailXml, "", org.jsoup.parser.Parser.xmlParser());

            // 获取文章元素
            Elements pubmedArticleList = document.select("PubmedArticle");
            if (pubmedArticleList.isEmpty()) {
                throw new IOException("No PubmedArticle found in XML");
            }

            Element pubmedArticle = pubmedArticleList.first();
            Element medlineCitation = null;
            if (pubmedArticle != null) {
                medlineCitation = pubmedArticle.selectFirst("MedlineCitation");
            }
            Element article = null;
            if (medlineCitation != null) {
                article = medlineCitation.selectFirst("Article");
            }
            
            // 从XML中提取基本信息
            String title = "";
            String journal = "";
            String issn = "";
            String volume = "";
            String issue = "";
            String pubDate = "";
            StringBuilder authorNames = new StringBuilder();
            String pages = "";
            if (article != null) {
                title = getElementTextContent(article, "ArticleTitle");
                // Journal Title
                journal = getElementTextContent(article, "Title");

                // 获取期刊信息
                Element journalElement = article.selectFirst("Journal");
                if (journalElement != null) {
                    issn = getElementTextContent(journalElement, "ISSN");
                    if (issn.isEmpty()) {
                        issn = getElementTextContent(journalElement, "ISSNLinking");
                    }
                }

                // 获取期刊卷号和期号
                Element journalIssueElement = article.selectFirst("JournalIssue");
                if (journalIssueElement != null) {
                    volume = getElementTextContent(journalIssueElement, "Volume");
                    issue = getElementTextContent(journalIssueElement, "Issue");

                    // 获取发表日期
                    Element pubDateElement = journalIssueElement.selectFirst("PubDate");
                    if (pubDateElement != null) {
                        String year = getElementTextContent(pubDateElement, "Year");
                        String month = getElementTextContent(pubDateElement, "Month");
                        String day = getElementTextContent(pubDateElement, "Day");

                        if (!year.isEmpty()) {
                            pubDate = year;
                            if (!month.isEmpty()) {
                                pubDate += "-" + month;
                                if (!day.isEmpty()) {
                                    pubDate += "-" + day;
                                }
                            }
                        }
                    }
                }

                // 解析作者信息
                Element authorList = article.selectFirst("AuthorList");
                if (authorList != null) {
                    Elements authorNodes = authorList.select("Author");
                    for (Element author : authorNodes) {
                        String lastName = getElementTextContent(author, "LastName");
                        String foreName = getElementTextContent(author, "ForeName");

                        if (!authorNames.toString().isEmpty()) {
                            authorNames.append(", ");
                        }

                        if (!foreName.isEmpty() && !lastName.isEmpty()) {
                            authorNames.append(foreName).append(" ").append(lastName);
                        } else if (!lastName.isEmpty()) {
                            authorNames.append(lastName);
                        }
                    }
                }

                // 解析页面信息
                Element pagination = article.selectFirst("Pagination");
                if (pagination != null) {
                    String startPage = getElementTextContent(pagination, "StartPage");
                    String endPage = getElementTextContent(pagination, "EndPage");

                    if (!startPage.isEmpty() && !endPage.isEmpty()) {
                        pages = startPage + "-" + endPage;
                    } else if (!startPage.isEmpty()) {
                        pages = startPage;
                    } else {
                        pages = getElementTextContent(pagination, "MedlinePgn");
                    }
                }
            }

            // 获取DOI信息
            String doi = "";
            Element pubmedData = null;
            if (pubmedArticle != null) {
                pubmedData = pubmedArticle.selectFirst("PubmedData");
            }
            if (pubmedData != null) {
                Element articleIdList = pubmedData.selectFirst("ArticleIdList");
                if (articleIdList != null) {
                    Elements articleIds = articleIdList.select("ArticleId");
                    for (Element articleId : articleIds) {
                        if ("doi".equals(articleId.attr("IdType"))) {
                            doi = articleId.text().trim();
                            break;
                        }
                    }
                }
            }

            // 提取摘要文本
            String abstractText = parseAbstractFromDocument(document);

            // 尝试获取引用次数（通过PMC数据库）
            int citationCount = 0;
            try {
                if (pmcJson != null) {
                    JsonNode pmcRoot = objectMapper.readTree(pmcJson);
                    JsonNode linkSets = pmcRoot.path("linksets");
                    if (!linkSets.isMissingNode() && !linkSets.isEmpty()) {
                        JsonNode linkSetDbs = linkSets.get(0).path("linksetdbs");
                        if (!linkSetDbs.isMissingNode()) {
                            // 查找pmc引用计数
                            for (JsonNode linkSetDb : linkSetDbs) {
                                if ("pmc".equals(linkSetDb.path("dbto").asText()) &&
                                    "pubmed_pmc".equals(linkSetDb.path("linkname").asText())) {
                                    citationCount = linkSetDb.path("links").size();
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Citation count fetch failed: {}", e.getMessage());
            }


            // 创建ResearchOutput对象并设置基本属性
            ResearchOutput researchOutput = new ResearchOutput();
            // 设置为论文类型
            researchOutput.setType(OutputType.PAPER);
            // 使用PubMed ID作为成果编号
            researchOutput.setOutputNumber(pubMedId);
            researchOutput.setTitle(title);
            researchOutput.setAbstractText(abstractText);
            researchOutput.setCitationCount(citationCount);
            researchOutput.setPublicationUrl("https://pubmed.ncbi.nlm.nih.gov/" + pubMedId + "/");
            researchOutput.setCreatedAt(null);
            researchOutput.setApproved(null);

            // 创建其他信息映射
            Map<String, Object> otherInfo = new HashMap<>();
            otherInfo.put("journal", journal);
            otherInfo.put("authors", authorNames.toString());
            otherInfo.put("pubDate", pubDate);
            otherInfo.put("doi", doi);
            otherInfo.put("volume", volume);
            otherInfo.put("issue", issue);
            otherInfo.put("pages", pages);
            otherInfo.put("issn", issn);
            
            // 从XML中提取额外信息
            extractAdditionalInfoFromDocument(document, otherInfo);
            
            researchOutput.setOtherInfo(otherInfo);

            log.info("Successfully fetched PubMed data and converted to ResearchOutput: {}", researchOutput);
            return researchOutput;
        } catch (Exception e) {
            log.error("Error parsing XML data: ", e);
            throw new IOException("Failed to parse XML data from PubMed", e);
        }
    }
    
    /**
     * 从XML元素中安全地获取文本内容
     * @param parentElement 父元素
     * @param tagName 标签名
     * @return 文本内容或空字符串
     */
    private String getElementTextContent(Element parentElement, String tagName) {
        if (parentElement == null) {
            return "";
        }
        
        Element element = parentElement.selectFirst(tagName);
        if (element != null) {
            return element.text().trim();
        }
        return "";
    }
    
    /**
     * 从已解析的Document中提取摘要文本
     * @param document 已解析的XML文档
     * @return 摘要文本
     */
    private String parseAbstractFromDocument(Document document) {
        try {
            Element abstractElement = document.selectFirst("AbstractText");
            if (abstractElement != null) {
                return abstractElement.text().trim();
            }
        } catch (Exception e) {
            log.warn("Failed to parse abstract from XML: {}", e.getMessage());
        }
        return "";
    }
    
    /**
     * 从已解析的Document中提取额外的信息
     * @param document 已解析的XML文档
     * @param otherInfo 其他信息映射
     */
    private void extractAdditionalInfoFromDocument(Document document, Map<String, Object> otherInfo) {
        try {            
            // 提取关键词
            Elements keywordElements = document.select("Keyword");
            if (!keywordElements.isEmpty()) {
                List<String> keywords = new ArrayList<>();
                for (Element keyword : keywordElements) {
                    keywords.add(keyword.text().trim());
                }
                otherInfo.put("keywords", keywords);
            }
            
            // 提取化学物质
            Elements chemicalElements = document.select("Chemical");
            if (!chemicalElements.isEmpty()) {
                List<Map<String, String>> chemicals = new ArrayList<>();
                for (Element chemicalElement : chemicalElements) {
                    String registryNumber = "";
                    String name = "";
                    
                    Element registryNumberElement = chemicalElement.selectFirst("RegistryNumber");
                    if (registryNumberElement != null) {
                        registryNumber = registryNumberElement.text().trim();
                    }
                    
                    Element nameOfSubstanceElement = chemicalElement.selectFirst("NameOfSubstance");
                    if (nameOfSubstanceElement != null) {
                        name = nameOfSubstanceElement.text().trim();
                    }
                    
                    if (!registryNumber.isEmpty() || !name.isEmpty()) {
                        Map<String, String> chemical = new HashMap<>();
                        chemical.put("registryNumber", registryNumber);
                        chemical.put("name", name);
                        chemicals.add(chemical);
                    }
                }
                if (!chemicals.isEmpty()) {
                    otherInfo.put("chemicals", chemicals);
                }
            }
            
            // 提取MeSH术语
            Elements meshElements = document.select("MeshHeading");
            if (!meshElements.isEmpty()) {
                List<Map<String, String>> meshTerms = new ArrayList<>();
                for (Element meshElement : meshElements) {
                    String ui = "";
                    String name = "";
                    
                    Element descriptorElement = meshElement.selectFirst("DescriptorName");
                    if (descriptorElement != null) {
                        ui = descriptorElement.attr("UI");
                        name = descriptorElement.text().trim();
                    }
                    
                    if (!ui.isEmpty() && !name.isEmpty()) {
                        Map<String, String> meshTerm = new HashMap<>();
                        meshTerm.put("ui", ui);
                        meshTerm.put("name", name);
                        meshTerms.add(meshTerm);
                    }
                }
                if (!meshTerms.isEmpty()) {
                    otherInfo.put("meshTerms", meshTerms);
                }
            }
            
            // 提取发表类型
            Elements pubTypeElements = document.select("PublicationType");
            if (!pubTypeElements.isEmpty()) {
                List<String> publicationTypes = new ArrayList<>();
                for (Element pubType : pubTypeElements) {
                    publicationTypes.add(pubType.text().trim());
                }
                otherInfo.put("publicationTypes", publicationTypes);
            }
            
            // 提取国家
            Element countryElement = document.selectFirst("Country");
            if (countryElement != null) {
                otherInfo.put("country", countryElement.text().trim());
            }
        } catch (Exception e) {
            log.warn("Failed to extract additional info from XML: {}", e.getMessage());
        }
    }
}