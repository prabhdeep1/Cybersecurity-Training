/**
 * Content Loader - Loads all text content from content.json
 * Sales team can update content.json without touching HTML
 */

async function loadContent() {
    try {
        const response = await fetch('content.json');
        if (!response.ok) {
            throw new Error(`Failed to load content.json: ${response.status}`);
        }
        const content = await response.json();
        
        // Update meta title
        if (content.meta && content.meta.title) {
            document.title = content.meta.title;
        }
        
        // Update navigation
        if (content.navigation) {
            const brandElement = document.querySelector('.brand');
            if (brandElement && content.navigation.brandName) {
                const brandIcon = content.meta?.brandIcon || '🛡️';
                brandElement.innerHTML = `<span class="brand-icon">${brandIcon}</span> ${content.navigation.brandName}`;
            }
            
            const navLinks = document.querySelector('.nav-links');
            if (navLinks && content.navigation.links) {
                navLinks.innerHTML = content.navigation.links.map(link => {
                    const ctaClass = link.isCta ? ' class="cta"' : '';
                    return `<li><a href="${link.href}"${ctaClass}>${link.text}</a></li>`;
                }).join('');
            }
        }
        
        // Update hero section
        if (content.hero) {
            const heroTitle = document.querySelector('.hero-text h1');
            if (heroTitle && content.hero.title) {
                // Replace \n with <br /> for line breaks
                heroTitle.innerHTML = content.hero.title.replace(/\n/g, '<br />');
            }
            
            const heroHighlight = document.querySelector('.hero-text .highlight');
            if (heroHighlight && content.hero.highlight) {
                heroHighlight.textContent = content.hero.highlight;
            }
            
            const heroDescription = document.querySelector('.hero-text p');
            if (heroDescription && content.hero.description) {
                heroDescription.textContent = content.hero.description;
            }
            
            const heroCta = document.querySelector('.hero-cta');
            if (heroCta && content.hero.ctaText) {
                heroCta.textContent = content.hero.ctaText;
                if (content.hero.ctaLink) {
                    heroCta.href = content.hero.ctaLink;
                }
            }
        }
        
        // Update Why Choose Us section
        if (content.whyChooseUs) {
            const whyChooseUsTitle = document.querySelector('#why-choose-us .section-header h2');
            if (whyChooseUsTitle && content.whyChooseUs.title) {
                whyChooseUsTitle.textContent = content.whyChooseUs.title;
            }
            
            const whyChooseUsCards = document.querySelectorAll('#why-choose-us .card');
            if (whyChooseUsCards && content.whyChooseUs.cards) {
                whyChooseUsCards.forEach((card, index) => {
                    if (content.whyChooseUs.cards[index]) {
                        const cardData = content.whyChooseUs.cards[index];
                        const titleElement = card.querySelector('h3');
                        const descElement = card.querySelector('p');
                        
                        if (titleElement && cardData.title) {
                            titleElement.textContent = cardData.title;
                        }
                        if (descElement && cardData.description) {
                            descElement.textContent = cardData.description;
                        }
                    }
                });
            }
        }
        
        // Update About section
        if (content.about) {
            const aboutTitle = document.querySelector('.about-content h2');
            if (aboutTitle && content.about.title) {
                aboutTitle.textContent = content.about.title;
            }
            
            const aboutDescription = document.querySelector('.about-content p');
            if (aboutDescription && content.about.description) {
                aboutDescription.textContent = content.about.description;
            }
            
            const missionCard = document.querySelector('.about-cards .card:nth-child(1)');
            if (missionCard && content.about.mission) {
                const missionTitle = missionCard.querySelector('h3');
                const missionDesc = missionCard.querySelector('p');
                if (missionTitle && content.about.mission.title) {
                    missionTitle.textContent = content.about.mission.title;
                }
                if (missionDesc && content.about.mission.description) {
                    missionDesc.textContent = content.about.mission.description;
                }
            }
            
            const visionCard = document.querySelector('.about-cards .card:nth-child(2)');
            if (visionCard && content.about.vision) {
                const visionTitle = visionCard.querySelector('h3');
                const visionDesc = visionCard.querySelector('p');
                if (visionTitle && content.about.vision.title) {
                    visionTitle.textContent = content.about.vision.title;
                }
                if (visionDesc && content.about.vision.description) {
                    visionDesc.textContent = content.about.vision.description;
                }
            }
        }
        
        // Update Services section
        if (content.services) {
            const servicesTitle = document.querySelector('.services-title');
            if (servicesTitle && content.services.title) {
                servicesTitle.textContent = content.services.title;
            }
            
            const serviceCards = document.querySelectorAll('.service-card');
            if (serviceCards && content.services.cards) {
                serviceCards.forEach((card, index) => {
                    if (content.services.cards[index]) {
                        const cardData = content.services.cards[index];
                        const titleElement = card.querySelector('h3');
                        const descElement = card.querySelector('p');
                        
                        if (titleElement && cardData.title) {
                            titleElement.textContent = cardData.title;
                        }
                        if (descElement && cardData.description) {
                            descElement.textContent = cardData.description;
                        }
                    }
                });
            }
        }
        
        // Update footer
        if (content.footer) {
            const footerText = document.querySelector('footer p');
            if (footerText && content.footer.copyright) {
                footerText.textContent = content.footer.copyright;
            }
        }
        
    } catch (error) {
        console.error('Error loading content:', error);
        // Fallback: Keep original HTML content if JSON fails to load
    }
}

// Load content when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', loadContent);
} else {
    loadContent();
}

